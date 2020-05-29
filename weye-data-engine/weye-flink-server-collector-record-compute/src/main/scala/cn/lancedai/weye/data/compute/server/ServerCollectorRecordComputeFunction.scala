package cn.lancedai.weye.data.compute.server

import cn.lancedai.weye.common.flink.rule.compute._
import cn.lancedai.weye.common.model.record.ServerCollectorRecord

object ServerCollectorRecordComputeFunction extends App {
  createDataComputeJob[ServerCollectorRecord](this.args)
}

