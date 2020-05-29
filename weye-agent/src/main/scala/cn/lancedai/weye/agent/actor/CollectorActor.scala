package cn.lancedai.weye.agent.actor

import akka.actor.Actor
import cn.lancedai.weye.common.NTO._
import cn.lancedai.weye.common.agent.Collector

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

abstract class CollectorActor[T >: Null <: Collector : ClassTag] extends Actor {

  def addCollector(collector: T): Try[_]

  def testCollectorConfig(collector: T): Try[_]

  def removeCollector(collector: T): Try[_]

  def _addCollector(addRequest: AddOp[T]): StatusResponse =
    handleRequest(addRequest,
      testCollectorConfig(addRequest.collector).flatMap(_ => addCollector(addRequest.collector))
    )

  def _removeCollector(removeRequest: RemoveOp[T]): StatusResponse = handleRequest(removeRequest, removeCollector(removeRequest.collector))

  def handleRequest(msg: CollectorOperationRequest[T], r: => Try[_]): StatusResponse =
  //持久化
    r.map((_: Any) => sqliteActor ! msg) match {
      case Failure(exception) =>
        exception.printStackTrace()
        FailureResponse(msg.id, exception.getMessage)
      case Success(value) => SuccessResponse(msg.id, value)
    }

  override def receive: Receive = {
    case mag: AddCollectorOperation[T] with CollectorOperationRequest[T] => sender() ! _addCollector(mag)
    case msg: RemoveCollectorOperation[T] with CollectorOperationRequest[T] => sender() ! _removeCollector(msg)
    case initList: List[T] => initList.foreach(addCollector(_: T));
  }
}
