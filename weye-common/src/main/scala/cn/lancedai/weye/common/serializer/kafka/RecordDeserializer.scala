package cn.lancedai.weye.common.serializer.kafka

import cn.lancedai.weye.common.model.record.BaseRecord
import cn.lancedai.weye.common.tool.SerializeTool
import cn.lancedai.weye.common.tool.SerializeTool._
import org.apache.kafka.common.serialization.Deserializer

class RecordDeserializer extends Deserializer[BaseRecord] {
  override def deserialize(topic: String, data: Array[Byte]): BaseRecord = {
    SerializeTool.deserialize(data, classOf[BaseRecord])
  }
}
