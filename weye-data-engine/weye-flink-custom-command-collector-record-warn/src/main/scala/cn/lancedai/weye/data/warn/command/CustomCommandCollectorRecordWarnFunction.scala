package cn.lancedai.weye.data.warn.command

import cn.lancedai.weye.common.model.record.{CustomCommandCollectorRecord, WarnRecord}
import cn.lancedai.weye.common.flink.rule.warn._
import org.apache.flink.streaming.api.scala._

object CustomCommandCollectorRecordWarnFunction extends App {
  createDataWarnJob[CustomCommandCollectorRecord](this.args)
}
