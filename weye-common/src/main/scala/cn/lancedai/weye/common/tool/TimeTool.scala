package cn.lancedai.weye.common.tool

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, Duration => JDuration}

import cn.lancedai.weye.common.emuration.TimePattern

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

object TimeTool {


  implicit def localDateTimeToTimeStamp(localDateTime: LocalDateTime): Timestamp = Timestamp.valueOf(localDateTime)

  implicit def stringToDuration(timeStr: String): JDuration = Duration.fromNanos(Duration(timeStr).toNanos).toJava

  def nowTimestamp(): Timestamp = LocalDateTime.now()

  private lazy val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

  def formatTimeStamp(timestamp: Timestamp): String = {
    formatTimeStamp(timestamp, formatter)
  }

  def formatTimeStamp(timestamp: Timestamp, pattern: String): String = {
    formatTimeStamp(timestamp, DateTimeFormatter.ofPattern(pattern))
  }

  def formatTimeStamp(timestamp: Timestamp, pattern: TimePattern): String = {
    formatTimeStamp(timestamp, DateTimeFormatter.ofPattern(pattern.getPattern))
  }

  def formatTimeStamp(timestamp: Timestamp, formatter: DateTimeFormatter): String =
    timestamp.toLocalDateTime.format(formatter)


  private val patternExample = "yyyy MM dd HH:mm:ss"

  // 时间戳格式化器，找出相距最远的两个时间戳，观察到那一项相等， 相等则不显示
  def createFormatsByTimestampList(timestampList: JList[Timestamp]): DateTimeFormatter = {
    if (timestampList.size() == 0) DateTimeFormatter.ofPattern(patternExample)
    else {
      implicit val keyOrdering: Ordering[Timestamp] = (x: Timestamp, y: Timestamp) => x.compareTo(y)
      val queue: mutable.Queue[String] = mutable.Queue(patternExample.split(" "): _*)
      val sortRes: mutable.Seq[Timestamp] = timestampList.asScala.sorted
      val min: LocalDateTime = sortRes.head.toLocalDateTime
      val max: LocalDateTime = sortRes.reverse.head.toLocalDateTime
      assert(max.compareTo(min) >= 0)
      if (max.getYear == min.getYear) {
        queue.dequeue
        if (max.getMonth == min.getMonth) {
          queue.dequeue
          if (max.getDayOfMonth == min.getDayOfMonth) queue.dequeue
        }
      }
      DateTimeFormatter.ofPattern(queue.foldLeft(new StringBuffer(queue.dequeue()))((sb: StringBuffer, str: String) => sb.append(s" $str")).toString)
    }
  }
}
