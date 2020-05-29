package cn.lancedai.weye.common.NTO

import cn.lancedai.weye.common.JSerializable
import cn.lancedai.weye.common.agent.{CustomCommandCollector, HttpRequestCollector}
import cn.lancedai.weye.common.model.ServerInfo

sealed trait AgentResponse extends JSerializable

case class ApiKeyCheckResponse(apiKey: String) extends AgentResponse

case class ServerInfoResponse(serverInfo: ServerInfo) extends AgentResponse

case class SyncResponse(customCommandCollectors: Array[Int], httpRequestCollectors: Array[Int]) extends AgentResponse


/**
 * 状态响应, 需要指明是对哪个请求的响应
 */
trait StatusResponse extends AgentResponse {
  def id: Int
}
/**
 * 成功状态响应
 */
case class SuccessResponse[+R](id: Int, data: R) extends StatusResponse

/**
 * 失败状态响应
 *
 * @param errMsg 异常信息
 */
case class FailureResponse(id: Int, errMsg: String) extends StatusResponse
