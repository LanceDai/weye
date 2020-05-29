package cn.lancedai.weye.agent

import java.io.{File, FileInputStream, FileNotFoundException}
import java.util.Properties
import java.util.concurrent.TimeUnit

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.reflect.io.{File => SFile}
import scala.util.{Failure, Success, Try}

object Config {


  private val log: Logger = LoggerFactory.getLogger(Config.getClass)
  private val CONFIG_PREFIX: String = "config"
  private val CONFIG_PROP_FILE_NAME: String = s"config.properties"
  private val LOG_CONFIG_FILE_NAME: String = s"logback.xml"
  private val AGENT_HOME: String = Option(System.getProperty("agent.home", System.getenv("AGENT_HOME"))).getOrElse {
    Thread.currentThread().getContextClassLoader.getResource("").getPath
  }

  //config property
  lazy val host: String = readConfig("process.center.host").split(":")(0)
  lazy val port: Int = readConfig("process.center.host").split(":")(1).toInt
  lazy val apiKey: String = readConfig("api.key")

  def fullConfigFilePath(filePath: String): String =
    s"$AGENT_HOME${File.separator}$CONFIG_PREFIX${File.separator}$filePath"


  // 启动初始化
  init()

  def init(): Unit = {
    //加载logback 配置文件
    val logbackConfigPath: String = fullConfigFilePath(LOG_CONFIG_FILE_NAME)
    val logbackFile: File = new File(logbackConfigPath)
    if (logbackFile.exists) {
//      TimeUnit.SECONDS.sleep(3)
      val lc: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
      val configurator: JoranConfigurator = new JoranConfigurator()
      configurator.setContext(lc)
      lc.reset();
      Try(configurator.doConfigure(logbackFile)) match {
        case Failure(exception) => exception match {
          case e: JoranException =>
            e.printStackTrace(System.err)
            System.exit(-1)
          case e: Exception => e.printStackTrace()
        }
        case Success(_) =>
          log.debug("load logback.xml success")
          log.debug(s"agent home in $AGENT_HOME")
      }
    }
  }

  def loadConfig(): Map[String, String] = {
    val properties: Properties = new Properties()
    val configPropertyPath: String = fullConfigFilePath(CONFIG_PROP_FILE_NAME)
    log.debug("load config in {}", configPropertyPath)
    Try {
      properties.load(new FileInputStream(configPropertyPath))
    } match {
      case Success(_) => // 加载时， 打印所有配置
        properties
          .asScala
          .map((tp: (String, String)) => {
            log.debug("property => {} --- {}", Seq(tp._1, tp._2): _*)
            tp
          }).toMap
      case Failure(exception) =>
        exception.printStackTrace()
        log.error("NO AGENT_HOME found or -Dagent.home=\" ??? \" set , please check your configuration")
        throw new FileNotFoundException()
    }
  }

  lazy val config: Map[String, String] = loadConfig()

  private def readConfig(key: String): String = config(key)

  private[agent] lazy val dbFilePath: String = {
    log.debug("get db File")
    val dbFilePath: String = getPath(readConfig("db.file.path"))
    SFile(dbFilePath).parent.createDirectory()
    dbFilePath
  }

  private[agent] lazy val downloadPath: String = getPath(readConfig("download.file.path"))

  //判断是否绝对路径
  private def getPath(tempPath: String): String =
    if (tempPath.startsWith(".")) AGENT_HOME + File.separator + tempPath else tempPath

}
