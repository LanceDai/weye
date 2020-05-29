package cn.lancedai.weye.agent.handler

import java.io.IOException

import akka.pattern.ask
import akka.util.Timeout
import cn.lancedai.weye.agent.actor._
import cn.lancedai.weye.agent.{AgentClient, Config}
import cn.lancedai.weye.common.NTO._
import cn.lancedai.weye.common.model.ServerInfo
import cn.lancedai.weye.common.tool.ExecutorTool.PlusScheduledExecutorService
import cn.lancedai.weye.common.tool.StringTool
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class AgentClientHandler extends SimpleChannelInboundHandler[ServerRequest] {

  self =>
  private val log: Logger = LoggerFactory.getLogger(self.getClass)
  implicit private val timeout: Timeout = Timeout(5 seconds)
  implicit private val plusExecutor: PlusScheduledExecutorService = executor
  private var ctx: ChannelHandlerContext = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    self.ctx = ctx
  }


  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    log.debug("close")
    self.ctx.close()
  }


  def handleSync(): Unit = {
    sqliteActor ? "init" onComplete {
      case Success(value) =>
        log.debug("value class: {}", value.getClass.getName)
        log.debug("value detail: {}", value.toString)
        self.ctx.writeAndFlush(value)
      case Failure(exception) => exception.printStackTrace()
    }
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ServerRequest): Unit = msg match {
    case ApiKeyCheckRequest() =>
      log.debug("apiKey check")
      ctx.writeAndFlush(ApiKeyCheckResponse(Config.apiKey))
    case InitRequest(serverIp, serverId, kafkaServers) =>
      log.debug("apiCheck ok, start init")
      init(kafkaServers, serverIp, serverId)
      val serverInfo: ServerInfo = serverRunningTimeCollectorActor.serverInfo
      log.debug("send server config => {}", serverInfo)
      ctx.writeAndFlush(ServerInfoResponse(serverInfo))
    case DownRequest(msg) =>
      log.debug("agent will down because of {}", msg)
      AgentClient.flag = true
      ctx.close()
    case msg: CollectorOperationRequest[_] => handleOperationRequest(msg)
    case TestCustomCommand(id, command) => handleTestCommand(id, command)
    case SyncRequest() => handleSync()
    case unknownRequest => log.warn(unknownRequest.getClass.getName)
  }


  def handleOperationRequest(msg: CollectorOperationRequest[_]): Unit = msg match {
    case req: CustomCommandCollectorOperation => handleRequest(req.id) {
      customCommandCollectorActor ? req
    }
    case req: HttpRequestCollectorOperation => handleRequest(req.id) {
      httpRequestCollectorActor ? req
    }
    case _ => log.error(_: String)
  }


  def handleRequest(id: Int)(f: => Future[_]): Unit = {
    f.onComplete {
      case Success(value: StatusResponse) =>
        value match {
          case FailureResponse(_, throwMsg) => log.debug("future execute ok, but res is fail: {}", throwMsg);
            self.ctx.writeAndFlush(FailureResponse(id, throwMsg))
          case res@SuccessResponse(_, _) => log.debug("ok")
            self.ctx.writeAndFlush(res)
        }
      case Failure(exception) => log.debug("future execute fail: {}", exception.getMessage);
        exception.printStackTrace()
        self.ctx.writeAndFlush(FailureResponse(id, exception.getMessage));
      case Success(value) => log.debug("unknown msg: {}", value.getClass.getSimpleName)
    }
  }

  @SuppressWarnings(Array("deprecation"))
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = cause match {
    case e: IOException =>
      log.debug("server is down or network break by: {}", cause.getMessage)
      ctx.close()
    case _ =>
      cause.printStackTrace()
      AgentClient.flag = true
      ctx.close()
  }

  def handleTestCommand(id: Int, command: String): Unit = handleRequest(id) {
    log.debug("command is {}", command)
    Future(Try(StringTool.stringToCommand(command))
      .map(_.!!) match {
      case Failure(exception) => FailureResponse(id, exception.getMessage)
      case Success(value) => SuccessResponse[String](id, value)
    })
  }

}
