package cn.lancedai.weye.data.compute.command

import cn.lancedai.weye.common.model.record.CustomCommandCollectorRecord
import cn.lancedai.weye.common.flink.rule.compute._

object CustomCommandCollectorRecordComputeFunction extends App {
  createDataComputeJob[CustomCommandCollectorRecord](this.args)
}
