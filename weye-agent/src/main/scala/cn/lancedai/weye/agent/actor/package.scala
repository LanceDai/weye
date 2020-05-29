package cn.lancedai.weye.agent

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ScheduledExecutorService, ThreadFactory}

import akka.actor.{ActorRef, ActorSystem, Props}
import cn.lancedai.weye.common.NTO.{AddCollectorOperation, CollectorOperationRequest, RemoveCollectorOperation}
import cn.lancedai.weye.common.agent.Collector

package object actor {
  val system: ActorSystem = ActorSystem.create("AgentActorSystem")

  // 初始化参数
  // kafka 链接
  var kafkaServers: String = _
  // serverIp  用于获取对外网卡
  var serverIp: String = _
  var serverId: Int = _

  def init(kafkaServers: String, serverIp: String, serverId: Int): Unit = {
    this.kafkaServers = kafkaServers
    this.serverId = serverId
    this.serverIp = serverIp
    serverRunningTimeCollectorActor.init()
  }

  //actor
  lazy val kafkaActor: ActorRef = system.actorOf(Props(classOf[KafkaActor], kafkaServers))
  lazy val customCommandCollectorActor: ActorRef = system.actorOf(Props(classOf[CustomCommandCollectorActor]))
  lazy val httpRequestCollectorActor: ActorRef = system.actorOf(Props(classOf[HttpRequestCollectorActor], serverIp))
  lazy val sqliteActor: ActorRef = system.actorOf(Props(classOf[SQLiteActor]))
  lazy val serverRunningTimeCollectorActor: ServerRunningTimeActor = new ServerRunningTimeActor

  //定时线程池
  val executor: ScheduledExecutorService =
    Executors.newScheduledThreadPool(Runtime.getRuntime.availableProcessors(),
      new ScheduledThreadFactory)

  class ScheduledThreadFactory extends ThreadFactory {
    private val atomicInteger: AtomicInteger = new AtomicInteger

    override def newThread(r: Runnable): Thread =
      new Thread(r, s"scheduled-task-${atomicInteger.incrementAndGet}")
  }

  // Collector
  type AddOp[+T <: Collector] = AddCollectorOperation[T] with CollectorOperationRequest[T]
  type RemoveOp[+T <: Collector] = RemoveCollectorOperation[T] with CollectorOperationRequest[T]
}
