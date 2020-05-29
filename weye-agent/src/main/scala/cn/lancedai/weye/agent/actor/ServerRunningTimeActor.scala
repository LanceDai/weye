package cn.lancedai.weye.agent.actor

import cn.lancedai.weye.common.model.ServerInfo
import cn.lancedai.weye.common.model.record.ServerCollectorRecord
import cn.lancedai.weye.common.tool.ExecutorTool._
import org.slf4j.{Logger, LoggerFactory}
import oshi.SystemInfo
import oshi.hardware.{CentralProcessor, GlobalMemory, HardwareAbstractionLayer, NetworkIF}
import oshi.software.os.OperatingSystem
import oshi.util.Util

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * 系统信息收集器， 是自动启动的
 */
class ServerRunningTimeActor {


  private val log: Logger = LoggerFactory.getLogger(classOf[ServerRunningTimeActor])

  private val duration: FiniteDuration = 5 seconds


  private[agent] val si: SystemInfo = new SystemInfo
  private[agent] val hal: HardwareAbstractionLayer = si.getHardware
  private[agent] val os: OperatingSystem = si.getOperatingSystem
  private[agent] val processor: CentralProcessor = hal.getProcessor
  private[agent] val memory: GlobalMemory = hal.getMemory
  private[agent] val networkIf: NetworkIF = hal.getNetworkIFs
    .filter((nif: NetworkIF) => nif.getIPv4addr.contains(serverIp)).head

  def init(): Unit = {
    executor.scheduleAtFixedRateWithDuration(runnableJob, duration)
  }

  /**
   * 用来定时获取服务器运行数据
   *
   * @return
   */
  private def runnableJob: Runnable = () => {
    val record: ServerCollectorRecord = new ServerCollectorRecord()
    val (cpuUsage, recvSpeed, sendSpeed) = computeData
    record.setServerId(serverId)
    record.setCpuUsage(cpuUsage)
    record.setMemUsage(getMemUsage)
    record.setThreadCount(os.getThreadCount)
    record.setCpuTemperature(hal.getSensors.getCpuTemperature)
    record.setContextSwitches(processor.getContextSwitches)
    record.setInterrupts(processor.getInterrupts)
    record.setRecvSpeed(recvSpeed)
    record.setSendSpeed(sendSpeed)
    kafkaActor ! record
  }


  /**
   * 获取服务器的配置信息，
   * 包括运行的操作系统，版本
   * CPU
   */
  def serverInfo: ServerInfo = new ServerInfo(
    serverId,
    createProcessorInfo(processor),
    os.getVersionInfo.getVersion,
    os.getFamily,
    os.getBitness,
    memory.getTotal
  )

  private[agent] def createProcessorInfo(processor: CentralProcessor): String =
    processor.getProcessorIdentifier.getName


  private[agent] def computeData: (Double, Long, Long) = {
    val prevTicks: Array[Long] = processor.getSystemCpuLoadTicks
    networkIf.updateAttributes()
    val recvBytes: Long = networkIf.getBytesRecv
    val sentBytes: Long = networkIf.getBytesSent
    Util.sleep(1000)
    networkIf.updateAttributes()
    (
      processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100d,
      networkIf.getBytesRecv - recvBytes,
      networkIf.getBytesSent - sentBytes
    )
  }

  def getMemUsage: Double = {
    1d * memory.getAvailable / memory.getTotal
  }
}
