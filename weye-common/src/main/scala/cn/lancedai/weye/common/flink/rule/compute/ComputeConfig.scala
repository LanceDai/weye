package cn.lancedai.weye.common.flink.rule.compute

import java.lang
import java.util.Properties

import cn.lancedai.weye.common.model.record.{BaseRecord, ComputeRecord}
import cn.lancedai.weye.common.model.rule.ComputeRule
import cn.lancedai.weye.common.exception.{UnSupportTypeException, UnknownFilterOpException}
import cn.lancedai.weye.common.flink.rule._
import cn.lancedai.weye.common.tool.ReflectTool._
import cn.lancedai.weye.common.tool.{SerializeTool, StringTool}
import javax.sql.DataSource
import org.apache.flink.api.common.functions.{FilterFunction, RichMapFunction}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer, KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.{Logger, LoggerFactory}
import wangzx.scala_commons.sql._

import scala.concurrent.duration.Duration
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

case class ComputeConfig[IN >: Null <: BaseRecord : ClassTag](
                                                               kafkaBootStrapServers: String,
                                                               computeRuleId: Int, //用来唯一标识消费组，避免竞争消费
                                                               username: String,
                                                               password: String,
                                                               dbUrl: String
                                                             ) {
  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val clazz: Class[IN] = classTag[IN].runtimeClass.asInstanceOf[Class[IN]]
  private val dataSource: DataSource = getMySQLDataSource(username, password, dbUrl)
  // 构建数据库连接， 获取
  val computeRule: ComputeRule = dataSource.row[Row](
    sql"SELECT id, name, source_type, source_id, filter_item, filter_op, filter_value, item, op, time, time_unit, web_app_id FROM WEYE.COMPUTE_RULE WHERE ID = $computeRuleId"
  ).map((row: Row) => {
    new ComputeRule(
      row.getInt("id"),
      row.getString("name"),
      row.getString("source_type"),
      row.getInt("source_id"),
      row.getString("filter_item"),
      row.getString("filter_op"),
      row.getString("filter_value"),
      row.getString("item"),
      row.getString("op"),
      row.getLong("time"),
      row.getString("time_unit"),
      row.getInt("web_app_id"),
    )
  }).orNull

  // 判断是否有后置处理
  def hasPostProcessing: Boolean = dataSource.row[Int](
    sql"""
          SELECT SUM(A.NUM) FROM (
            SELECT COUNT(*) AS NUM
                FROM WEYE.COMPUTE_RULE
                WHERE SOURCE_TYPE = 'COMPUTE'
                AND SOURCE_ID =  $computeRuleId
            UNION ALL
            SELECT COUNT(*) AS NUM
                FROM WEYE.WARN_RULE
                WHERE SOURCE_TYPE = 'COMPUTE'
                AND SOURCE_ID = $computeRuleId
          ) AS A;
         """
  ) match {
    case Some(value) => value > 0
    case None => false
  }

  def getTimeWindowSize: Time = Time.of(computeRule.getTime, Duration(s"1${computeRule.getTimeUnit}")._2)

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
        case v: String => v == computeRule.getFilterValue.asInstanceOf[String]
        case v: Double => Try(computeRule.getFilterValue.toDouble)
          .flatMap(doubleValueFilter(v, _: Double, computeRule.getFilterOp)) match {
          case Failure(exception) => throw exception
          case Success(value) => value
        }
        case x => throw new UnSupportTypeException(x.getClass)
      }
    }

    if (computeRule.getFilterItem == "*") true else {
      Try(clazz.getDeclaredField(computeRule.getFilterItem).withAccessible().get(value))
        .flatMap(filterValue(_: AnyRef)) match {
        case Failure(exception) => throw exception
        case Success(value) => value
      }
    }
  }

  def getRecordConsumer: FlinkKafkaConsumer[IN] = {
    val props: Properties = new Properties()
    val topicPrefix: String = StringTool.humpToChar(clazz.getSimpleName, "-")
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, s"$topicPrefix-COMPUTE-CONSUMER-$computeRuleId")

    new FlinkKafkaConsumer(s"$topicPrefix-${computeRule.getSourceId}",
      new RecordSchema[IN](clazz), props)
  }

  def createComputeRecord: RichMapFunction[Double, ComputeRecord] = (data: Double) => {
    val record = new ComputeRecord(computeRuleId, data)
    println(s"record = ${record}")
    record
  }

  def getSourceProducer: FlinkKafkaProducer[ComputeRecord] = {
    val props: Properties = new Properties()
    val topicPrefix: String = StringTool.humpToChar(classOf[ComputeRecord].getSimpleName, "-")
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers)
    val topic = s"$topicPrefix-$computeRuleId"
    new FlinkKafkaProducer[ComputeRecord](topic,
      new ComputeRecordSchema(topic), props,
      FlinkKafkaProducer.Semantic.AT_LEAST_ONCE
    )
  }

  def getSelectFunction: RichMapFunction[IN, Double] = (value: IN) => Try(clazz.getDeclaredField(computeRule.getItem).get(value).asInstanceOf[Double]) match {
    case Failure(exception) => throw exception
    case Success(value) => value
  }


  // AggregateFunction 聚合操作
  def getMinFunction: MinFunction[IN] = new MinFunction[IN](computeRule.getItem)

  def getMaxFunction: MaxFunction[IN] = new MaxFunction[IN](computeRule.getItem)

  def getSumFunction: SumFunction[IN] = new SumFunction[IN](computeRule.getItem)

  def getCountFunction: CountFunction[IN] = new CountFunction[IN](computeRule.getItem)

  def getAvgFunction: AvgFunction[IN] = new AvgFunction[IN](computeRule.getItem)


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

class ComputeRecordSchema(topic: String) extends KafkaSerializationSchema[ComputeRecord] {
  def serialize(element: ComputeRecord): Array[Byte] =
    SerializeTool.serialize(element) match {
      case Failure(exception) => throw exception
      case Success(value) => value
    }

  override def serialize(element: ComputeRecord, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] =
    new ProducerRecord[Array[Byte], Array[Byte]](topic, serialize(element))
}

// sink
class MySQLSink(dataSource: DataSource) extends RichSinkFunction[ComputeRecord] {

  // 调用连接 执行sql
  override def invoke(value: ComputeRecord, context: SinkFunction.Context[_]): Unit = {
    println("write -> " + value.toString)
    // 插入新纪录
    dataSource executeUpdate sql"INSERT INTO WEYE.COMPUTE_RECORD(COMPUTE_RULE_ID, DATA, TIMESTAMP) VALUES (${value.getComputeRuleId}, ${value.getData}, ${value.getTimestamp})"
  }
}


