package cn.lancedai.weye.data.warn.server

import cn.lancedai.weye.common.flink.rule.warn.createDataWarnJob
import cn.lancedai.weye.common.model.record.ServerCollectorRecord

object ServerCollectorRecordWarnFunction extends App {
  createDataWarnJob[ServerCollectorRecord](this.args)
}
