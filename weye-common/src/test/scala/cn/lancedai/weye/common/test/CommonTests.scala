package cn.lancedai.weye.common.test

import java.net.{InetAddress, NetworkInterface}
import java.sql.Timestamp
import java.time.LocalDateTime

import cn.lancedai.weye.common.model.record.ServerCollectorRecord
import cn.lancedai.weye.common.tool.{StringTool, TimeTool}
import org.scalatest.{FlatSpecLike, Matchers}
import sun.net.util.IPAddressUtil

import scala.util.{Success, Try}


class A {

  self =>

  import scala.beans.BeanProperty

  @BeanProperty var a: Int = _
  @BeanProperty var b: String = _

  def this(a: Int, b: String) = {
    this()
    self.a = a
    self.b = b
  }
}

//@Ignore
class CommonTests extends FlatSpecLike with Matchers {

  import scala.language.postfixOps

  "A TimeUnit Tests" should "right" in {
    import java.util.concurrent.TimeUnit
    val timeUnit = TimeUnit.SECONDS
    val time = 5;
    println(timeUnit.toMillis(time))
  }

  "A JSON Tool" should "help me parsing object to JHashMap" in {
    import cn.lancedai.weye.common.tool.JSONTool
    val res = JSONTool.objectToMap(new A(1, "1111"))
    res.forEach((k, v) => println(s"key = $k --- value = $v"))
  }
  "Hump To Char" should "change to my mind" in {
    val className = classOf[ServerCollectorRecord].getSimpleName
    val res = StringTool.humpToChar(className, "-")
    val res1 = StringTool.humpToLine(className)
    println(s"className = $className")
    println(s"res = ${res}")
    println(s"res1 = ${res1}")
  }


  "A timestamp sort tool" must "be test" in {
    val timestampList = new java.util.ArrayList[Timestamp]
    val startTime: LocalDateTime = LocalDateTime.now()
    println("1. year diff")
    timestampList.add(Timestamp.valueOf(startTime))
    timestampList.add(Timestamp.valueOf(startTime.plusYears(2)))
    val res1 = TimeTool.createFormatsByTimestampList(timestampList)
    println("res =" + res1)
    timestampList.clear()
    println("2. year same, month diff")
    timestampList.add(Timestamp.valueOf(startTime))
    timestampList.add(Timestamp.valueOf(startTime.plusMonths(2)))
    val res2 = TimeTool.createFormatsByTimestampList(timestampList)
    println("res =" + res2)
    timestampList.clear()
    println("3. year same, month same, day diff")
    timestampList.add(Timestamp.valueOf(startTime))
    timestampList.add(Timestamp.valueOf(startTime.plusDays(2)))
    val res3 = TimeTool.createFormatsByTimestampList(timestampList)
    println("res =" + res3)
    timestampList.clear()
    println("4. year same, month same, day same")
    timestampList.add(Timestamp.valueOf(startTime))
    timestampList.add(Timestamp.valueOf(startTime.plusMinutes(2)))
    val res4 = TimeTool.createFormatsByTimestampList(timestampList)
    println("res =" + res4)
  }

  import scala.collection.JavaConverters._

  "A address be captured" should "connect to address" in {

    def sameNetwork(ip: String, addrs: List[String]): String = {
      if (IPAddressUtil.isIPv4LiteralAddress(ip))
        addrs.filter(IPAddressUtil.isIPv4LiteralAddress).head
      else if (IPAddressUtil.isIPv6LiteralAddress(ip))
        addrs.filter(IPAddressUtil.isIPv6LiteralAddress).head
      else null
    }

    def captureHost(address: InetAddress): String = NetworkInterface.getNetworkInterfaces
      .asScala.filter(address.isReachable(_, 2000, 2000))
      .map(_.getInetAddresses.asScala.toList)
      .filter(_.size == 2)
      .map(_.map(_.getHostAddress))
      .map(sameNetwork(address.getHostAddress, _)).next()

    for {
      ip <- Success("172.17.90.11")
      address <- Try(InetAddress.getByName(ip))
      host <- Try(captureHost(address))
    } yield println(s"host = $host")
  }

  "field check" should "not be fail" in {
    val clazz: Class[ServerCollectorRecord] = classOf[ServerCollectorRecord]

    def check(item: String): Boolean =
      clazz.getDeclaredField(item) != null

    def check2(item: String): Boolean =
      clazz.getSuperclass.getDeclaredField(item) != null
//    val res = check("timestamp")
    val res2 = check2("timestamp")
//    println(s"res = ${res}")
  }

}
