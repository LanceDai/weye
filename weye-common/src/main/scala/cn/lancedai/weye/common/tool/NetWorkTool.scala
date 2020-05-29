package cn.lancedai.weye.common.tool

import java.net.{InetAddress, NetworkInterface}

import sun.net.util.IPAddressUtil

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object NetWorkTool {
  // 判断是否是IPV4地址
  def isIPv4(ip: String): Boolean =
    "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z".r.pattern.matcher(ip).matches()

  // 判断是否是IPV6地址
  def isIPv6(ip: String): Boolean =
  //无全0块，标准IPv6地址的正则表达式
    "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$".r.
      pattern.matcher(ip).matches() ||
      "^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4})*)?)::((([0-9A-Fa-f]{1,4}:)*[0-9A-Fa-f]{1,4})?)$".r
        .pattern.matcher(ip).matches() //压缩正则表达式


  def getCorrespondingHost(ip: String): String = {

    def sameNetwork(ip: String, addrs: List[String]): String = {
      if (isIPv4(ip))
        addrs.filter(isIPv4).head
      else if (isIPv6(ip))
        addrs.filter(isIPv6).head
      else null
    }

    def captureHost(address: InetAddress): String = NetworkInterface.getNetworkInterfaces
      .asScala.filter(address.isReachable(_, 2000, 2000))
      .map(_.getInetAddresses.asScala.toList)
      .filter(_.size == 2)
      .map(_.map(_.getHostAddress))
      .map(sameNetwork(address.getHostAddress, _)).next()

    Try(InetAddress.getByName(ip))
      .map(captureHost) match {
      case Failure(exception) => exception.printStackTrace(); null
      case Success(value) => value
    }
  }
}
