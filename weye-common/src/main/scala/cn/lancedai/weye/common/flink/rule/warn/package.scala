package cn.lancedai.weye.common.flink.rule

import cn.lancedai.weye.common.model.record.{BaseRecord, WarnRecord}
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import scala.language.reflectiveCalls
import scala.reflect.ClassTag

package object warn {
  def createDataWarnJob[IN >: Null <: BaseRecord : ClassTag](args: Array[String]): Unit = {

    def initConfig(args: Array[String]): WarnConfig[IN] = {
      val kafkaBootStrapServers: String = args(0)
      val warnRuleId: Int = args(1).toInt
      // mysql 配置
      val username: String = args(2)
      val password: String = args(3)
      val dbUrl: String = args(4)
      WarnConfig[IN](
        kafkaBootStrapServers, warnRuleId,
        username, password, dbUrl
      )
    }

    val warnConfig: WarnConfig[IN] = initConfig(args)
    // 构建流处理环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 配置处理环境的并发度为4
    env.setParallelism(1)
    // 非常关键，一定要设置启动检查点！！ 使得可以恢复
    //    env.enableCheckpointing(5000)

    // 自定义数据流（单例）
    val dataStream: DataStream[WarnRecord] =
      env.addSource(warnConfig.getRecordConsumer.setStartFromLatest())
        .setParallelism(1)
        .filter(warnConfig.getFilterFunction) //若filter有值，证明出现异常
        .map((_: IN).getId)
        .map(warnConfig.createWarnRecord)
    // sink 处理
    // 一方面发送至kafka, 实时发送警告
    dataStream.addSink(warnConfig.getSourceProducer)
    dataStream.addSink(warnConfig.getMySQLJdbcSink)
    // 懒加载执行
    env.execute(s"WARN-${warnConfig.warnRuleId}")
  }
}
