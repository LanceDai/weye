package cn.lancedai.weye.agent.actor

import java.net.InetAddress
import java.util
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

import cn.lancedai.weye.common.NTO.FullHttpRequestCollectorRecord
import cn.lancedai.weye.common.agent.HttpRequestCollector
import cn.lancedai.weye.common.exception.NoApplicationListeningException
import cn.lancedai.weye.common.model.record.{HttpRequestCollectorRecord, HttpRequestHeader}
import cn.lancedai.weye.common.tool.PcapTool._
import cn.lancedai.weye.common.tool.StringTool._
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{FullHttpRequest, HttpHeaders, HttpObjectAggregator, HttpServerCodec}
import org.pcap4j.core._
import org.pcap4j.packet.{IpPacket, Packet, TcpPacket}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class HttpRequestCollectorActor(serverIp: String) extends CollectorActor[HttpRequestCollector] {
  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  // 保证 port 与 sourceId 一一 对应
  private val sourceMap = new ConcurrentHashMap[Int, HttpRequestCollector]()
  private val portMap = new ConcurrentHashMap[Int, Int]()

  // 用来标志网卡数据捕获是否已开启
  private val state = new AtomicBoolean(false)
  private lazy val initJob: PcapHandle => Runnable = (handler: PcapHandle) => () => {
    while (!Thread.currentThread().isInterrupted) {
      try {
        // 如果是收到的请求, 写入
        // 由于监听到的是同一张网卡的所有信息, 一定会出现不同客户端的tcp混杂在一起
        // 所以第一步需要把把每一个链接区分开来, 争对每一个链接, 绑定一个channel
        val packet: Packet = handler.getNextPacketEx
        val ip: IpPacket = packet.get(classOf[IpPacket])
        val tcp: TcpPacket = packet.get(classOf[TcpPacket])
        //        log.debug("ip: {}", ip.getHeader.getSrcAddr)
        //        log.debug("tcp: {}", tcp.toString)
        Option(tcp)
          .map((_: TcpPacket).getHeader.getDstPort.valueAsInt())
          .map((dstPort: Int) => (dstPort, portMap.containsKey(dstPort)))
          .map((tp: (Int, Boolean)) =>
            if (tp._2) {
              //保证开始进行监听
              val source: HttpRequestCollector = sourceMap.get(portMap.get(tp._1))
              ListenChannel.addChannel(source, ip, tcp)
            } else {
              ListenChannel.removeChannelInPort(tp._1)
            }
          )
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

  def init(): Unit = {
    state.synchronized {
      if (state.get()) {
        log.error("can not init twice")
      } else {
        val networkInterface: PcapNetworkInterface = Pcaps.getDevByAddress(InetAddress.getByName(serverIp))
        val handle: PcapHandle = networkInterface.
          openLive(65536, PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS, 10)
        //柏克莱封包过滤器, 获取所有TCP包数据
        handle.setFilter("tcp", BpfProgram.BpfCompileMode.NONOPTIMIZE)
        executor.submit(initJob(handle))
        state.set(true)
      }
    }
  }

  override def addCollector(collector: HttpRequestCollector): Try[_] = {
    if (!state.get()) init()
    Try(sourceMap.put(collector.webAppId, collector))
      .map((_: HttpRequestCollector) => portMap.put(collector.port, collector.webAppId))
  }


  override def testCollectorConfig(collector: HttpRequestCollector): Try[_] =
    Try(testPort(collector.port))

  override def removeCollector(collector: HttpRequestCollector): Try[_] = {
    Option(sourceMap.remove(collector.webAppId))
      .map((x: HttpRequestCollector) => {
        portMap.remove(x.port)
        ListenChannel.removeChannelInPort(x.port)
      }) match {
      case Some(value) => Success(value)
      case None => Failure(new Throwable(s"delete fail, no id is  ${collector.webAppId}"))
    }
  }

  //测试相关函数
  def testPort(port: Int): Boolean = {
    try {
      val res: String = stringToCommand(s"netstat -ano | grep -w $port | grep LISTEN").!!
      if (res != null && res.nonEmpty) true else throw new NoApplicationListeningException
    } catch {
      case _: RuntimeException => throw new NoApplicationListeningException
    }
  }

  private class HttpRequestHandler(webAppId: Int, ip: String, srcPort: Int) extends SimpleChannelInboundHandler[FullHttpRequest] {


    override def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest): Unit = {
      kafkaActor ! FullHttpRequestCollectorRecord(
        new HttpRequestCollectorRecord(
          webAppId,
          msg.uri(),
          msg.method().name(),
          HttpRequestCollectorActor.getIpAddress(msg, ip),
          srcPort,
          msg.protocolVersion().text()),
        httpRequestHeaders(msg)
      )
    }

    def httpRequestHeaders(msg: FullHttpRequest): Array[HttpRequestHeader] = {
      msg.headers()
        .entries()
        .asScala
        .map((entry: util.Map.Entry[String, String]) =>
          new HttpRequestHeader(entry.getKey, entry.getValue)
        ).toArray
    }
  }


  private case class ListenChannel(sourceId: Int, ip: String, srcPort: Int) extends EmbeddedChannel(
    new HttpServerCodec,
    new HttpObjectAggregator(512 * 1024),
    new HttpRequestHandler(sourceId, ip, srcPort)
  )

  private object ListenChannel {

    private val tcpSessionMap = new ConcurrentHashMap[String, ListenChannel](1024)

    //存储port对应session， 方便垃圾回收
    private val portKeyMap = new ConcurrentHashMap[Int, util.List[String]]()

    def addChannel(agentHttpSource: HttpRequestCollector, ipPackage: IpPacket, tcpPacket: TcpPacket) {
      val key: String = (ipPackage, tcpPacket)
      val rawData: Packet = tcpPacket.getPayload
      val isFin: Boolean = tcpPacket.getHeader.getFin
      val hostAddress: String = ipPackage.getHeader.getSrcAddr.getHostAddress
      val dstPort: Int = tcpPacket.getHeader.getDstPort.valueAsInt()
      val srcPort: Int = tcpPacket.getHeader.getSrcPort.valueAsInt()
      // 判断session 是否已存在
      Option(tcpSessionMap.get(key)) match {
        case Some(session) =>
          //session 存在, 判断是否结束, 结束则删除
          if (isFin) {
            log.debug("channel destroy")
            val list: util.List[String] = portKeyMap.get(dstPort)
            list.remove(key)
            if (list.size() == 0) portKeyMap.remove(dstPort)
            tcpSessionMap.remove(key, session)
          } else if (rawData != null) {
            log.debug("channel has new data")
            session.writeInbound(
              Unpooled.buffer.writeBytes(rawData.getRawData)
            )
          } else {
            log.debug("empty package")
          }
        case None =>
          log.debug("new channel init -- {} ", key)
          // 不存在, 则初始化channel 并添加至map中
          tcpSessionMap.put(key, new ListenChannel(
            agentHttpSource.webAppId,
            hostAddress,
            srcPort
          ))
          val list: util.ArrayList[String] = new util.ArrayList[String]()
          list.add(key)
          portKeyMap.put(dstPort, list)
      }
    }

    def removeChannelInPort(dstPort: Int): Unit = {
      Option(portKeyMap.remove(dstPort)).foreach((_: util.List[String]).asScala.foreach(tcpSessionMap.remove(_: String)))
    }
  }

}

object HttpRequestCollectorActor {

  def getIpAddress(request: FullHttpRequest, ip: String): String = {
    def isEmptyIp(ip: String): Option[String] = if (ip == null || ip.length() == 0) None else Some(ip)

    def isLocalIp(ip: String): Option[String] =
      if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) None else Some(ip)

    val header: HttpHeaders = request.headers()
    isEmptyIp(header.get("x-forwarded-for"))
      .orElse(isEmptyIp(header.get("Proxy-Client-IP")))
      .orElse(isEmptyIp(header.get("WL-Proxy-Client-IP")))
      .orElse(isEmptyIp(header.get("HTTP_CLIENT_IP")))
      .orElse(isEmptyIp(header.get("HTTP_X_FORWARDED_FOR")))
      .orElse(isLocalIp(ip))
      .getOrElse(InetAddress.getLocalHost.getHostAddress)
  }
}
