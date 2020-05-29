package cn.lancedai.weye.agent.test

import java.util.Properties

import cn.lancedai.weye.agent.Config
import cn.lancedai.weye.common.model.record.BaseRecord
import cn.lancedai.weye.common.serializer.kafka.RecordSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.scalatest.{FlatSpec, Ignore}

@Ignore
class KafkaProducerTests extends FlatSpec {
//  private val TOPIC: String = "agent-record"
//
//  lazy val kafkaSender: KafkaProducer[String, BaseRecord] = {
//    val props: Properties = new Properties()
//    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "49.234.25.252:9092,139.9.5.184:9092,120.79.181.75:9092")
//    props.put(ProducerConfig.ACKS_CONFIG, Config.kafkaProducerConfig(ProducerConfig.ACKS_CONFIG))
//    props.put(ProducerConfig.RETRIES_CONFIG, Config.kafkaProducerConfig(ProducerConfig.RETRIES_CONFIG))
//    props.put(ProducerConfig.BATCH_SIZE_CONFIG, Config.kafkaProducerConfig(ProducerConfig.BATCH_SIZE_CONFIG))
//    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, Config.kafkaProducerConfig(ProducerConfig.BUFFER_MEMORY_CONFIG))
//    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[RecordSerializer])
//    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[RecordSerializer])
//    new KafkaProducer[String, BaseRecord](props)
//  }

  //  "kafka send msg" must "have a response" in {
  //
  //    val record = ExecuteCommandSourceRecordResponse(new ExecuteCommandRecord(1, 0.2))
  //    val res = kafkaSender.send(
  //      new ProducerRecord(TOPIC,
  //        record
  //      )
  //    ).get()
  //    println(s"res.toString = ${res.toString}")
  //  }
  //  "kafka send execute command record" must "have a response" in {
  //
  //    val record = ExecuteCommandSourceRecordResponse(new ExecuteCommandRecord(1, 0.2))
  //    val res = kafkaSender.send(
  //      new ProducerRecord("agent-record",
  //        record
  //      )
  //    ).get()
  //    println(s"res.toString = ${res.toString}")
  //  }


}
