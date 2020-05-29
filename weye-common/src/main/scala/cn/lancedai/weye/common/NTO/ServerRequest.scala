package cn.lancedai.weye.common.NTO

import cn.lancedai.weye.common.JSerializable
import cn.lancedai.weye.common.agent.{Collector, CustomCommandCollector, HttpRequestCollector}

sealed trait ServerRequest extends JSerializable


/* 控制请求*/

case class ApiKeyCheckRequest() extends ServerRequest

case class InitRequest(serverIp: String, serverId: Int, kafkaServers: String) extends ServerRequest

case class SyncRequest() extends ServerRequest

case class DownRequest(msg: String) extends ServerRequest

/**
 * 操作请求， 需要得到回应
 */
sealed trait OperationRequest extends ServerRequest {
  def id: Int
}

case class TestCustomCommand(id: Int, command: String) extends OperationRequest

/**
 * 数据采集器相关请求
 *
 * @tparam R Agent 用于存储和计算的对象
 */
sealed trait CollectorOperationRequest[+R <: Collector] extends OperationRequest {
  def collector: R
}

/**
 * 添加操作，需要将其转换为Collector，用于存储
 */
sealed trait AddCollectorOperation[+R <: Collector] extends JSerializable

sealed trait RemoveCollectorOperation[+R <: Collector] extends JSerializable

// 采集器操作
// HttpRequestCollector 操作
sealed trait HttpRequestCollectorOperation extends CollectorOperationRequest[HttpRequestCollector]

// 添加HttpRequestCollector
case class AddHttpRequestCollector(override val id: Int, webAppId: Int, port: Int) extends AddCollectorOperation[HttpRequestCollector] with HttpRequestCollectorOperation {
  override def collector: HttpRequestCollector = HttpRequestCollector(webAppId, port)
}

// 移除HttpRequestCollector
case class RemoveHttpRequestCollector(override val id: Int, webAppId: Int) extends RemoveCollectorOperation[HttpRequestCollector] with HttpRequestCollectorOperation {
  override def collector: HttpRequestCollector = HttpRequestCollector.removeSignObj(webAppId)
}

/**
 * CustomCommandCollector op
 */
sealed trait CustomCommandCollectorOperation extends CollectorOperationRequest[CustomCommandCollector]

/**
 * 自定义命令数据采集器 添加
 *
 * @param id          请求ID
 * @param subscribeId 订阅ID
 * @param command     执行命令
 * @param duration    执行间隔时间
 */
case class AddCustomCommandCollector(override val id: Int, subscribeId: Int, command: String, duration: String) extends AddCollectorOperation[CustomCommandCollector] with CustomCommandCollectorOperation {
  override def collector: CustomCommandCollector = CustomCommandCollector(subscribeId, command, duration)
}

/**
 * 可执行数据源添加 移除
 */
case class RemoveCustomCommandCollector(override val id: Int, subscribeId: Int) extends RemoveCollectorOperation[CustomCommandCollector] with
  CustomCommandCollectorOperation {
  override def collector: CustomCommandCollector = CustomCommandCollector.removeSignObj(subscribeId)
}
