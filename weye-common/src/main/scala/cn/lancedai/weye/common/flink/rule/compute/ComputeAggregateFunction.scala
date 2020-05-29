package cn.lancedai.weye.common.flink.rule.compute

import java.lang.reflect.Field

import cn.lancedai.weye.common.exception.NoSuchItemInClassException
import cn.lancedai.weye.common.flink.rule._
import org.apache.flink.api.common.accumulators._
import org.apache.flink.api.common.functions.AggregateFunction

import scala.reflect.{ClassTag, classTag}
import scala.util.Try

abstract class ComputeAggregateFunction[IN >: Null : ClassTag, F <: SimpleAccumulator[JDouble]](item: String) extends AggregateFunction[IN, F, Double] {
  //初始化检查
  val clazz: Class[IN] = classTag[IN].runtimeClass.asInstanceOf[Class[IN]]
  if (!check(item)) throw new NoSuchItemInClassException(item, clazz)

  def check(item: String): Boolean = Try {
    if (item == "*") this.isInstanceOf[CountFunction[IN]]
    else clazz.getDeclaredField(item)
  }.isSuccess

  override def add(value: IN, accumulator: F): F = {
    val field: Field = clazz.getDeclaredField(item)
    field.setAccessible(true)
    accumulator.add(field.getDouble(value))
    accumulator
  }

  override def getResult(accumulator: F): Double = accumulator.getLocalValue

  override def merge(a: F, b: F): F = {
    a.merge(b)
    a
  }
}

class MinFunction[IN >: Null : ClassTag](item: String) extends ComputeAggregateFunction[IN, DoubleMinimum](item) {
  override def createAccumulator(): DoubleMinimum = new DoubleMinimum()
}

class MaxFunction[IN >: Null : ClassTag](item: String) extends ComputeAggregateFunction[IN, DoubleMaximum](item) {
  override def createAccumulator(): DoubleMaximum = new DoubleMaximum()
}

class SumFunction[IN >: Null : ClassTag](item: String) extends ComputeAggregateFunction[IN, DoubleSum](item) {
  override def createAccumulator(): DoubleSum = new DoubleSum
}

class AvgFunction[IN >: Null : ClassTag](item: String) extends ComputeAggregateFunction[IN, AverageAccumulator](item) {
  override def createAccumulator(): AverageAccumulator = new AverageAccumulator
}

class CountFunction[IN >: Null : ClassTag](item: String) extends ComputeAggregateFunction[IN, DoubleCounter](item) {
  override def add(value: IN, accumulator: DoubleCounter): DoubleCounter = {
    accumulator.add(1d)
    accumulator
  }

  override def createAccumulator(): DoubleCounter = new DoubleCounter()
}


case class DoubleSum() extends SimpleAccumulator[JDouble] {

  private var sum: Double = 0d

  // ------------------------------------------------------------------------
  //  Accumulator
  // ------------------------------------------------------------------------

  override def add(value: JDouble): Unit = {
    this.sum += value
  }

  override def getLocalValue: JDouble = sum

  override def merge(other: Accumulator[JDouble, JDouble]): Unit = {
    this.sum += other.getLocalValue
  }

  override def resetLocal(): Unit = {
    this.sum = 0d
  }

  override def clone: DoubleSum = {
    val clone: DoubleSum = new DoubleSum
    clone.sum = this.sum
    clone
  }

  // ------------------------------------------------------------------------
  //  Utilities
  // ------------------------------------------------------------------------
  override def toString: String = "DoubleSUm " + this.sum
}
