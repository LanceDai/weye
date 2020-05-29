package cn.lancedai.weye.common.flink.rule

import cn.lancedai.weye.common.model.record.{BaseRecord, ComputeRecord}
import org.apache.flink.streaming.api.datastream.{AllWindowedStream, DataStream}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

import scala.reflect.ClassTag

package object compute {
  def createDataComputeJob[IN >: Null <: BaseRecord : ClassTag](args: Array[String]): Unit = {
    def initConfig(args: Array[String]): ComputeConfig[IN] = {
      val kafkaBootStrapServers: String = args(0)
      val computeRuleId: Int = args(1).toInt
      // mysql 配置
      val username: String = args(2)
      val password: String = args(3)
      val dbUrl: String = args(4)
      ComputeConfig[IN](
        kafkaBootStrapServers, computeRuleId,
        username, password, dbUrl
      )
    }

    val computeConfig: ComputeConfig[IN] = initConfig(args)
    // 构建流处理环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // 配置处理环境的并发度为1
    env.setParallelism(1)
    // 非常关键，一定要设置启动检查点！！ 使得可以恢复
    //    env.enableCheckpointing(5000)

    // 自定义数据流（单例）
    val dataStream: AllWindowedStream[IN, TimeWindow] =
      env.addSource(computeConfig.getRecordConsumer.setStartFromLatest())
        .setParallelism(1)
        .filter(computeConfig.getFilterFunction)
        .timeWindowAll(computeConfig.getTimeWindowSize)
    // 根据配置执行聚合运算
    val aggregateDataStream: DataStream[Double] = {
      if (computeConfig.computeRule.getOp.equalsIgnoreCase("SUM")) dataStream.aggregate(computeConfig.getSumFunction)
      else if (computeConfig.computeRule.getOp.equalsIgnoreCase("MIN")) dataStream.aggregate(computeConfig.getMinFunction)
      else if (computeConfig.computeRule.getOp.equalsIgnoreCase("MAX")) dataStream.aggregate(computeConfig.getMaxFunction)
      else if (computeConfig.computeRule.getOp.equalsIgnoreCase("COUNT")) dataStream.aggregate(computeConfig.getCountFunction)
      else dataStream.aggregate(computeConfig.getAvgFunction)
    }
    val computeRecordDataStream: DataStream[ComputeRecord] = aggregateDataStream.map(computeConfig.createComputeRecord)
    // sink 处理
    // 处理成计算结果
    // 判断计算规则是否有后置处理
    // 若有，则发送至topic，作为后置处理
    if (computeConfig.hasPostProcessing) computeRecordDataStream.addSink(computeConfig.getSourceProducer)
    // 若无， 则直接入库
    computeRecordDataStream.addSink(computeConfig.getMySQLJdbcSink)
    // test
    computeRecordDataStream.print()
    // 懒加载执行
    env.execute(s"COMPUTE-${computeConfig.computeRuleId}")
  }
}
