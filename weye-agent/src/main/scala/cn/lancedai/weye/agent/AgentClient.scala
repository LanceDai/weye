package cn.lancedai.weye.agent

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.Terminated
import cn.lancedai.weye.agent.handler.AgentClientHandler
import cn.lancedai.weye.common.tool.ExecutorTool._
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption, EventLoopGroup}
import io.netty.handler.codec.serialization.{ClassResolvers, ObjectDecoder, ObjectEncoder}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

class AgentClient {
  implicit val executor: PlusScheduledExecutorService = AgentClient.executor
  private val log: Logger = AgentClient.log
  private var bootstrap: Bootstrap = _
  private var group: EventLoopGroup = _

  private var waitTime: Long = 1

  private val connectTimeOut: FiniteDuration = 5 seconds


  def this(host: String, port: Int) {
    this()
    group = new NioEventLoopGroup
    this.bootstrap = new Bootstrap
    bootstrap.group(group)
      .channel(classOf[NioSocketChannel])
      .remoteAddress(new InetSocketAddress(host, port))
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(connectTimeOut.toMillis.toInt))
      .option(ChannelOption.TCP_NODELAY, java.lang.Boolean.valueOf(true))
      .handler(
        new ChannelInitializer[SocketChannel]() {
          override protected def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline
              //添加POJO对象解码器 禁止缓存类加载器
              .addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.cacheDisabled(this.getClass.getClassLoader)))
              // 设置发送消息编码器
              .addLast(new ObjectEncoder)
              .addLast(new AgentClientHandler)
          }
        })
  }

  private def connect(): Unit = {
    try {
      val f: ChannelFuture = bootstrap.connect.sync()
      waitTime = 1
      log.debug("connect server")
      f.channel.closeFuture.sync()
      log.debug("stop connect server")
    } finally {
      if (AgentClient.flag || waitTime >= 100) {
        log.error("Agent should close, because of flag is {}, or wait time > 100", AgentClient.flag)
        //直接关闭
        group.shutdownGracefully.sync
        actor.system.terminate()
          .onComplete((_: Try[Terminated]) => actor.executor.shutdownNow())
      } else {
        waitTime = waitTime << 1
        log.debug("start sleep {}s", waitTime)
        TimeUnit.SECONDS.sleep(waitTime)
        log.debug("start reconnect")
        connect()
      }
    }
  }

}

object AgentClient {
  // 是否退出标志
  private[agent] var flag: Boolean = false

  implicit val executor: PlusScheduledExecutorService = actor.executor
  private val log: Logger = LoggerFactory.getLogger(AgentClient.getClass)

  @throws[InterruptedException] def main(args: Array[String]): Unit = {
    val host: String = Config.host
    val port: Int = Config.port
    Future(AgentClient(host, port).connect())
    log.info("Agent start")
  }

  def apply(host: String, port: Int): AgentClient = new AgentClient(host, port)
}
