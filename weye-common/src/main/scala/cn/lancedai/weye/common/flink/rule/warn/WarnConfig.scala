package cn.lancedai.weye.common.flink.rule.warn

import java.lang
import java.util.Properties

import cn.lancedai.weye.common.model.record.{BaseRecord, WarnRecord}
import cn.lancedai.weye.common.model.rule.WarnRule
import cn.lancedai.weye.common.exception.{UnSupportTypeException, UnknownFilterOpException}
import cn.lancedai.weye.common.flink.rule._
import cn.lancedai.weye.common.tool.ReflectTool._
import cn.lancedai.weye.common.tool.{SerializeTool, StringTool}
import javax.sql.DataSource
import org.apache.flink.api.common.functions.{FilterFunction, RichMapFunction}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer, KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.{Logger, LoggerFactory}
import wangzx.scala_commons.sql._

import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

case class WarnConfig[IN >: Null <: BaseRecord : ClassTag](
                                                            kafkaBootStrapServers: String,
                                                            warnRuleId: Int, //用来唯一标识消费组，避免竞争消费
                                                            username: String,
                                                            password: String,
                                                            dbUrl: String
                                                          ) {
  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val clazz: Class[IN] = classTag[IN].runtimeClass.asInstanceOf[Class[IN]]
  private val dataSource: DataSource = getMySQLDataSource(username, password, dbUrl)
  // 构建数据库连接， 获取
  val warnRule: WarnRule = dataSource.row[Row](
    sql"SELECT id, name, source_type, source_id, compare_item, compare_op, compare_value, warn_way, warn_url, warn_msg, web_app_id FROM WEYE.WARN_RULE WHERE ID = $warnRuleId"
  ).map((row: Row) => {
    new WarnRule(
      row.getInt("id"),
      row.getString("name"),
      row.getString("source_type"),
      row.getInt("source_id"),
      row.getString("compare_item"),
      row.getString("compare_op"),
      row.getString("compare_value"),
      row.getString("warn_way"),
      row.getString("warn_url"),
      row.getString("warn_msg")
    )
  }).orNull

  def getFilterFunction: FilterFunction[IN] = (value: IN) => {
    def doubleValueFilter(compareValue: Double, filterValue: Double, filterOp: String): Try[Boolean] = Try {
      filterOp match {
        case ">" => compareValue > filterValue
        case ">=" => compareValue >= filterValue
        case "<" => compareValue < filterValue
        case "<=" => compareValue <= filterValue
        case "=" => compareValue == filterValue
        case _ => throw new UnknownFilterOpException()
      }
    }

    def filterValue(value: Any): Try[Boolean] = Try {
      value match {
        case v: String => v == warnRule.getCompareValue
        case v: Double => Try(warnRule.getCompareValue.toDouble)
          .flatMap(doubleValueFilter(v, _: Double, warnRule.getCompareOp)) match {
          case Failure(exception) => throw exception
          case Success(value) => value
        }
        case x => throw new UnSupportTypeException(x.getClass)
      }
    }

    Try(clazz.getDeclaredField(warnRule.getCompareItem).withAccessible().get(value))
      .flatMap(filterValue(_: AnyRef)) match {
      case Failure(exception) => throw exception
      case Success(value) => value
    }
  }

  def getRecordConsumer: FlinkKafkaConsumer[IN] = {
    val props: Properties = new Properties()
    val topicPrefix: String = StringTool.humpToChar(clazz.getSimpleName, "-")
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, s"$topicPrefix-WARN-CONSUMER-$warnRuleId")

    new FlinkKafkaConsumer(s"$topicPrefix-${warnRule.getSourceId}",
      new RecordSchema[IN](clazz), props)
  }

  def createWarnRecord: RichMapFunction[Int, WarnRecord] = (recordId: Int) => new WarnRecord(warnRuleId, recordId)

  def getSourceProducer: FlinkKafkaProducer[WarnRecord] = {
    val props: Properties = new Properties()
    val topic: String = StringTool.humpToChar(classOf[WarnRecord].getSimpleName, "-")
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers)
    new FlinkKafkaProducer[WarnRecord](topic,
      new WarnRecordSchema(topic),
      props,
      FlinkKafkaProducer.Semantic.AT_LEAST_ONCE
    )
  }

  // 写入数据库
  //写入mysql
  def getMySQLJdbcSink: MySQLSink = new MySQLSink(dataSource)
}


// kafka 序列化与反序列化
class RecordSchema[IN >: Null <: BaseRecord : ClassTag](clazz: Class[IN]) extends KafkaDeserializationSchema[IN] {
  //  private val recordDeserializer = new RecordDeserializer

  override def isEndOfStream(nextElement: IN): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): IN =
    SerializeTool.deserialize(record.value(), classOf[BaseRecord]) match {
      case Failure(exception) => throw exception
      case Success(value) => value.asInstanceOf[IN]
    }

  override def getProducedType: TypeInformation[IN] = TypeInformation.of(clazz)
}

class WarnRecordSchema(topic: String) extends KafkaSerializationSchema[WarnRecord] {
  def serialize(element: WarnRecord): Array[Byte] =
    SerializeTool.serialize(element) match {
      case Failure(exception) => throw exception
      case Success(value) => value
    }

  override def serialize(element: WarnRecord, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] =
    new ProducerRecord[Array[Byte], Array[Byte]](topic, serialize(element))
}

// sink
class MySQLSink(dataSource: DataSource) extends RichSinkFunction[WarnRecord] {

  // 调用连接 执行sql
  override def invoke(value: WarnRecord, context: SinkFunction.Context[_]): Unit = {
    // 插入新纪录
    dataSource executeUpdate sql"INSERT INTO WEYE.WARN_RECORD(WARN_RULE_ID, STATUS, TIMESTAMP) VALUES (${value.getWarnRuleId}, ${value.getStatus}, ${value.getTimestamp})"
  }
}


