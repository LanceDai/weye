package cn.lancedai.weye.common.agent

import cn.lancedai.weye.common.JSerializable

//DTO
sealed trait Collector extends JSerializable

case class CustomCommandCollector(subscribeId: Int, command: String, duration: String) extends Collector

object CustomCommandCollector {
  def removeSignObj(subscribeId: Int): CustomCommandCollector = CustomCommandCollector(subscribeId, "", "")
}

case class HttpRequestCollector(webAppId: Int, port: Int) extends Collector

object HttpRequestCollector {
  def removeSignObj(webAppId: Int): HttpRequestCollector = HttpRequestCollector(webAppId, 0)
}


