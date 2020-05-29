package cn.lancedai.weye.agent.test

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import akka.util.Timeout
import cn.lancedai.weye.agent.actor._
import cn.lancedai.weye.common.NTO.{AddCustomCommandCollector, AddHttpRequestCollector}
import cn.lancedai.weye.common.tool.ExecutorTool.PlusScheduledExecutorService
import org.scalatest.{FlatSpec, Ignore}

import scala.language.postfixOps

//@Ignore
class CommonTests extends FlatSpec {
//  "mvn project" must "have a test" in {
//    import java.io.File
//    val directory = new File("..")
//    val filePath1 = directory.getAbsolutePath;
//    println(s"filePath1 = ${filePath1}")
//    val filepath2 = System.getProperty("user.dir")
//    println(s"filepath2 = ${filepath2}")
//  }
//  "pattern match test" must "do all" in {
//    def handler(msg: AddOp[_]): Unit = msg match {
//      case AddCustomCommandCollector(_, _, command, duration) =>
//        println(s"command => $command -- duration => $duration")
//      case AddHttpRequestCollector(_, webAppId, port) =>
//        println(s"port => $port")
//    }
//
//    def handler2(msg: AddOp[_]): Unit = msg match {
//      case op: AddCustomCommandCollector =>
//        println(s"command => ${op.command} -- duration => ${op.duration}")
//      case op: AddHttpRequestCollector =>
//        println(s"port => ${op.port}")
//    }
//
//    handler(AddCustomCommandCollector(1, 2, "aa", "1000s"))
//    handler2(AddCustomCommandCollector(1, 2, "aa", "1000s"))
//    handler(AddHttpRequestCollector(1, 2, 99999))
//    handler2(AddHttpRequestCollector(1, 2, 99999))
//  }
//  "pattern match test2" must "do all" in {
//    import scala.concurrent.duration._
//    implicit val plusExecutor: PlusScheduledExecutorService = executor
//    implicit val timeout: Timeout = 3 seconds
//    val actorRef = TestActorRef.create[SQLiteActor](ActorSystem(), Props(classOf[SQLiteActor]))
//    val actor = actorRef.underlyingActor
//    actor.receive(AddHttpRequestCollector(1, 2, 99999))
//  }


    "throwable test" must "do all" in {
      try{
        1 / 0
      }catch {
        case throwable: Throwable =>{
          val newThrowable = new Throwable(throwable.getMessage)
          newThrowable.getStackTrace
        }
      }
    }
}
