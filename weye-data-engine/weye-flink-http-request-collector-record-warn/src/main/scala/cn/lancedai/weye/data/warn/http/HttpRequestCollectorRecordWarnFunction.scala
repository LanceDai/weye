package cn.lancedai.weye.data.warn.http

import cn.lancedai.weye.common.flink.rule.warn._
import cn.lancedai.weye.common.model.record.HttpRequestCollectorRecord

object HttpRequestCollectorRecordWarnFunction extends App {
  createDataWarnJob[HttpRequestCollectorRecord](this.args)
}
