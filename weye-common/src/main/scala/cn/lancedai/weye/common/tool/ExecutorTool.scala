package cn.lancedai.weye.common.tool

import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture}

import cn.lancedai.weye.common.exception.ScheduleFutureCancelFail

import scala.concurrent.duration.Duration

object ExecutorTool {

  import scala.concurrent.ExecutionContext


  implicit class PlusScheduledExecutorService(executor: ScheduledExecutorService) extends ExecutionContext {
    def scheduleAtFixedRateWithDuration(command: Runnable, duration: Duration): ScheduledFuture[_] = {
      executor.scheduleAtFixedRate(command, 0, duration._1, duration._2)
    }

    override def execute(runnable: Runnable): Unit = executor.execute(runnable)

    override def reportFailure(cause: Throwable): Unit = cause.printStackTrace()


  }

  implicit class PlusScheduledFuture[T](future: ScheduledFuture[T]) {


    def cancelWithException(): Unit =
      if (!future.cancel(true)) throw new ScheduleFutureCancelFail()
  }

}
