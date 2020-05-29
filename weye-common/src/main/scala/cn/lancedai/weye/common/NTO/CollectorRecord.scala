package cn.lancedai.weye.common.NTO

import cn.lancedai.weye.common.model.record._


// 特殊返回结果
sealed trait CollectorRecord

/**
 * HttpRequestCollector采集信息
 */
class FullHttpRequestCollectorRecord(val httpRequestCollectorRecord: HttpRequestCollectorRecord, val httpRequestHeaderRecords: Array[HttpRequestHeader]) extends BaseRecord
object FullHttpRequestCollectorRecord{
  def apply(httpRequestCollectorRecord: HttpRequestCollectorRecord, httpRequestHeaderRecords: Array[HttpRequestHeader]): FullHttpRequestCollectorRecord = new FullHttpRequestCollectorRecord(httpRequestCollectorRecord, httpRequestHeaderRecords)
}



