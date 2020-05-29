package cn.lancedai.weye.agent.actor

import java.util.Properties

import akka.actor.Actor
import cn.lancedai.weye.agent.Config
import cn.lancedai.weye.common.NTO.FullHttpRequestCollectorRecord
import cn.lancedai.weye.common.model.record.{BaseRecord, CustomCommandCollectorRecord, ServerCollectorRecord}
import cn.lancedai.weye.common.serializer.kafka.RecordSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}

import scala.compat.java8.DurationConverters._
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * kafkaActor 用于把执行结果发送给Server端
 */
class KafkaActor(kafkaServers: String) extends Actor {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val closeTimeOut: FiniteDuration = 3 seconds
  private val topic: String = "AGENT-RECORD"

  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers)
  props.put(ProducerConfig.ACKS_CONFIG, "all")
  props.put(ProducerConfig.RETRIES_CONFIG, Integer.valueOf(0))
  props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.valueOf(16384))
  props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, Integer.valueOf(33554432))
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[RecordSerializer])

  val producer: KafkaProducer[String, BaseRecord] = new KafkaProducer(props)

  override def receive: Receive = {
    //利用ApiKey作为Server的唯一标识
    case recordRes: ServerCollectorRecord => send(recordRes)
    case recordResponse: FullHttpRequestCollectorRecord => send(recordResponse)
    case recordResponse: CustomCommandCollectorRecord => send(recordResponse)
    case unknownResponse =>
      log.debug("unknow request: {}", unknownResponse.getClass.getName)
  }

  def send(data: BaseRecord): Unit = producer.send(new ProducerRecord(topic, Config.apiKey, data))

  override def postStop(): Unit = {
    producer.close(closeTimeOut.toJava)
  }
}
