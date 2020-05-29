package cn.lancedai.weye.agent.test

import org.scalatest.FlatSpec
import oshi.hardware._

import scala.util.{Failure, Success, Try}


//@Ignore
class ServerInfoTests extends FlatSpec {

  import oshi.SystemInfo
  import oshi.hardware.HardwareAbstractionLayer
  import oshi.software.os.OperatingSystem

  val si: SystemInfo = new SystemInfo
  val hal: HardwareAbstractionLayer = si.getHardware
  val os: OperatingSystem = si.getOperatingSystem
  "Server Info" must "show os type" in {
    //    key = getFileSystem -- oshi.software.os.linux.LinuxFileSystem@15b986cd
    //    key = getVersion -- unknown (unknown) build 4.19.84-microsoft-standard
    //    key = getFamily -- Ubuntu
    //    key = getManufacturer -- GNU/Linux
    //    key = getVersionInfo -- 18.04.4 LTS (Bionic Beaver) build 4.19.84-microsoft-standard
    //    key = getProcesses -- [Loshi.software.os.OSProcess;@6bb7cce7
    //      key = getProcessId -- 971
    //    key = getProcessCount -- 13
    //    key = getThreadCount -- 481
    //    key = getBitness -- 64
    //    key = getSystemUptime -- 955
    //    key = getSystemBootTime -- 1589283039
    classOf[OperatingSystem].getMethods
      .filter(_.getName.startsWith("get"))
      .map(method => (method.getName, Try(method.invoke(os))))
      .filter(tp => tp._2.isSuccess)
      .foreach(tp => println(s"key = ${tp._1} -- ${tp._2.get.toString}"))
    classOf[SystemInfo].getMethods
      .filter(_.getName.startsWith("get"))
      .map(method => (method.getName, Try(method.invoke(si))))
      .filter(tp => tp._2.isSuccess)
      .foreach(tp => println(s"key = ${tp._1} -- ${tp._2.get.toString}"))
    classOf[HardwareAbstractionLayer].getMethods
      .filter(_.getName.startsWith("get"))
      .map(method => (method.getName, Try(method.invoke(hal))))
      .filter(tp => tp._2.isSuccess)
      .foreach(tp => println(s"key = ${tp._1} -- ${tp._2.get.toString}"))
  }

  "Server Info" must "show thing I need" in {
    println(s"os.getVersionInfo = ${os.getVersionInfo}")
    println(s"os.getFamily = ${os.getFamily}")

    os.getServices.foreach(
      osService => println(
        s"""
           |name = ${osService.getName}"
           |processId = ${osService.getProcessID}
           |state = ${osService.getState}
           |""".stripMargin
      )
    )
    println(s"os.getBitness = ${os.getBitness}")
    hal.getPowerSources.foreach(
      powerSource => showObjProp(classOf[PowerSource], powerSource)
    )
    hal.getDiskStores.foreach(
      diskStore => showObjProp(classOf[HWDiskStore], diskStore)
    )
    hal.getDisplays.foreach(
      display => showObjProp(classOf[Display], display)
    )
    hal.getSoundCards.foreach(
      soundCard => showObjProp(classOf[SoundCard], soundCard)
    )
    hal.getGraphicsCards.foreach(
      g => showObjProp(classOf[GraphicsCard], g)
    )
    showObjProp(classOf[CentralProcessor], hal.getProcessor)
    println("processorName = "+ hal.getProcessor.getProcessorIdentifier.getName)
    println("processorName logic = "+ hal.getProcessor.getLogicalProcessors)
  }

  def showObjProp[T](clazz: Class[T], obj: T): Unit = {
    println(clazz.getName)
    clazz.getMethods
      .map(method => (method.getName, Try(method.invoke(hal))))
      .map(tp => {
        val message: String = tp._2 match {
          case Failure(exception) => exception.getMessage
          case Success(value) => "true"
        }
        println(s"key = ${tp._1} -- $message")
        tp
      })
      .filter(tp => tp._2.isSuccess)
      .foreach(tp => println(s"key = ${tp._1} -- ${tp._2.get.toString}"))
  }
}
