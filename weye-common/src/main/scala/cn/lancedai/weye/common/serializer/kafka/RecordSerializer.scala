package cn.lancedai.weye.common.serializer.kafka

import cn.lancedai.weye.common.model.record.BaseRecord
import cn.lancedai.weye.common.tool.SerializeTool
import cn.lancedai.weye.common.tool.SerializeTool._
import org.apache.kafka.common.serialization.Serializer

class RecordSerializer extends Serializer[BaseRecord] {
  override def serialize(topic: String, data: BaseRecord): Array[Byte] = {
    SerializeTool.serialize(data)
  }
}
