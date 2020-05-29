package cn.lancedai.weye.agent.actor

import akka.actor.Actor
import cn.lancedai.weye.common.agent.{Collector, CustomCommandCollector}
import org.slf4j.{Logger, LoggerFactory}
import org.sqlite.JDBC

//保存记录至SQLite
import cn.lancedai.weye.agent.Config
import cn.lancedai.weye.common.NTO._
import cn.lancedai.weye.common.agent.HttpRequestCollector
import javax.sql.DataSource
import org.sqlite.SQLiteDataSource
import wangzx.scala_commons.sql._

class SQLiteActor extends Actor {

  private val log: Logger = LoggerFactory.getLogger(classOf[SQLiteActor])
  private implicit val dataSource: DataSource = {
    val dataSource: SQLiteDataSource = new SQLiteDataSource()
    dataSource.setUrl(s"${JDBC.PREFIX}${Config.dbFilePath}")
    dataSource
  }

  // 执行初始化
  override def preStart: Unit = {
    log.debug("start init")
    // 判断相关表是否已存在, 若不存在初始化
    // 1. CUSTOM_COMMAND_COLLECTOR
    if (!tableExist("CUSTOM_COMMAND_COLLECTOR"))
      dataSource executeUpdate
        sql"""
           CREATE TABLE CUSTOM_COMMAND_COLLECTOR(
            SUBSCRIBE_ID INTEGER UNIQUE ,
            COMMAND VARCHAR(20),
            DURATION VARCHAR(200)
           )
           """
    else //若存在, 则发送消息给指定的Actor
      customCommandCollectorActor ! dataSource.rows[CustomCommandCollector](sql"""select * from CUSTOM_COMMAND_COLLECTOR""")

    if (!tableExist("HTTP_REQUEST_COLLECTOR"))
      dataSource executeUpdate
        sql"""
          CREATE TABLE HTTP_REQUEST_COLLECTOR(
            WEB_APP_ID INTEGER UNIQUE ,
            PORT INTEGER
          )
         """
    else //若存在, 则发送消息给指定的Actor
      httpRequestCollectorActor ! dataSource.rows[HttpRequestCollector](sql"""select * from HTTP_REQUEST_COLLECTOR"""
      )
  }

  private def addCollector(collector: Collector): Unit = collector match {
    case CustomCommandCollector(subscribeId, command, duration) =>
      dataSource executeUpdate
        sql"""INSERT INTO CUSTOM_COMMAND_COLLECTOR(SUBSCRIBE_ID, COMMAND, DURATION) VALUES ($subscribeId, $command, $duration)"""
    case HttpRequestCollector(webAppId, port) =>
      dataSource executeUpdate
        sql"""INSERT INTO HTTP_REQUEST_COLLECTOR(WEB_APP_ID, PORT) VALUES ($webAppId, $port)"""
    case value => log.debug("handlerAddRequest have unknown op: {}", value.getClass.getName)
  }

  private def removeCollector(collector: Collector): Unit = collector match {
    case CustomCommandCollector(subscribeId, _, _) =>
      dataSource executeUpdate
        sql"""DELETE FROM  CUSTOM_COMMAND_COLLECTOR WHERE SUBSCRIBE_ID = $subscribeId"""
    case HttpRequestCollector(webAppId, _) =>
      dataSource executeUpdate
        sql"""DELETE FROM HTTP_REQUEST_COLLECTOR WHERE WEB_APP_ID = $webAppId"""
    case value => log.debug("handlerRemoveRequest have unknown op: {}", value.getClass.getName)
  }


  override def receive: Receive = {
    case msg: AddCustomCommandCollector => addCollector(msg.collector)
    case msg: AddHttpRequestCollector => addCollector(msg.collector)
    case msg: RemoveCustomCommandCollector => removeCollector(msg.collector)
    case msg: RemoveHttpRequestCollector => removeCollector(msg.collector)
    case "init" => log.debug("sqlActor run normal")
      sender() ! SyncResponse(
        dataSource.rows[CustomCommandCollector](sql"""select * from CUSTOM_COMMAND_COLLECTOR""")
          .map(_.subscribeId).toArray,
        dataSource.rows[HttpRequestCollector](sql"""select * from HTTP_REQUEST_COLLECTOR""")
          .map(_.webAppId).toArray,
      )
    case x => log.debug(s"nothing => $x")
  }

  private def tableExist(tableName: String): Boolean =
    dataSource.row[Int](
      sql"""select count(*) from sqlite_master where type='table' and name = $tableName"""
    ) match {
      case Some(value) => value > 0
      case None => false
    }
}

