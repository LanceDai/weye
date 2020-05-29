package cn.lancedai.weye.common.tool

import org.pcap4j.packet.{IpPacket, TcpPacket}

import scala.language.implicitConversions

object PcapTool {


  implicit def IpPackageToString(packet: (IpPacket, TcpPacket)): String = {
    s"${packet._1.getHeader.getSrcAddr.getHostAddress}-${packet._2.getHeader.getSrcPort.value()}" +
      s"-${packet._1.getHeader.getDstAddr.getHostAddress}-${packet._2.getHeader.getDstPort.value()}"
  }
}
