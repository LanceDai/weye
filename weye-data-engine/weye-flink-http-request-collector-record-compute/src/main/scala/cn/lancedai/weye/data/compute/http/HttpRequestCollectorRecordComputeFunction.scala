package cn.lancedai.weye.data.compute.http

import cn.lancedai.weye.common.flink.rule.compute._
import cn.lancedai.weye.common.model.record.HttpRequestCollectorRecord

object HttpRequestCollectorRecordComputeFunction extends App {
  createDataComputeJob[HttpRequestCollectorRecord](this.args)
}
