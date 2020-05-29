package cn.lancedai.weye.agent.actor

import java.util.concurrent.{ConcurrentHashMap, ScheduledFuture}

import cn.lancedai.weye.common.agent.CustomCommandCollector
import cn.lancedai.weye.common.model.record.CustomCommandCollectorRecord
import cn.lancedai.weye.common.tool.CollectionTool._
import cn.lancedai.weye.common.tool.ExecutorTool._
import cn.lancedai.weye.common.tool.StringTool._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scala.util.Try

class CustomCommandCollectorActor extends CollectorActor[CustomCommandCollector] {


  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val collectorMap: ConcurrentHashMap[Int, ScheduledFuture[_]] = new ConcurrentHashMap[Int, ScheduledFuture[_]]()
  private val customCommandMap: ConcurrentHashMap[String, List[Int]] = new ConcurrentHashMap[String, List[Int]]()


  private def sendRecord(data: Double, command: String): Unit = {
    customCommandMap.get(command).foreach((subscribeId: Int) => {
      val record = new CustomCommandCollectorRecord()
      record.setData(data)
      record.setSubscribeId(subscribeId)
      log.debug("custom record is {}", record)
      kafkaActor ! record
    }
    )
  }

  /**
   * 判断是否已存在
   *
   * @param collector 采集器
   * @return
   */
  override def addCollector(collector: CustomCommandCollector): Try[_] = {
    if (!customCommandMap.containsKey(collector.command)) {
      // 此命令还未注册过
      customCommandMap.put(collector.command, List(collector.subscribeId))
      Try(executor.scheduleAtFixedRateWithDuration(
        () => sendRecord(
          stringToCommand(collector.command).!!.toDouble,
          collector.command
        ), Duration(collector.duration))
      ).map((future: ScheduledFuture[_]) => collectorMap.put(collector.subscribeId, future))
    } else {
      Try(customCommandMap.computeIfPresent(
        collector.command,
        (_: String, value: List[Int]) => value.::(collector.subscribeId))
      )
    }
  }


  override def testCollectorConfig(collector: CustomCommandCollector): Try[_] = {
    //下载初始化脚本
    Try(stringToCommand(collector.command).!!.toDouble)
  }

  override def removeCollector(collector: CustomCommandCollector): Try[_] =
    Try(collectorMap.removeWithException(collector.subscribeId))
      .flatten
      .map((_: ScheduledFuture[_]).cancelWithException())
}