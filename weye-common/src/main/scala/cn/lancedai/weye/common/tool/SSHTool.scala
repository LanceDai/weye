package cn.lancedai.weye.common.tool

import java.io.{BufferedReader, InputStream, InputStreamReader}

import ch.ethz.ssh2.{Connection, Session}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object SSHTool {

  def open(username: String, password: String, host: String, port: Int = 22): SSHSession =
    Try(new Connection(host, port))
      .map((conn: Connection) => {
        conn.connect()
        conn
      })
      .map((conn: Connection) => {
        if (!conn.authenticateWithPassword(username, password))
          throw new RuntimeException(s"$host -- $username -- $password login failed.")
        else SSHSession(conn)
      }) match {
      case Failure(e) => throw e
      case Success(session) => session
    }

  case class SSHSession(connection: Connection) {
    private val log: Logger = LoggerFactory.getLogger(classOf[SSHSession])

    private def isSudo(sudo: Boolean): String = if (sudo) "sudo" else ""


    private def which(command: String, sudo: Boolean): String =
      execute(sudo, "which", Array("--skip-alias", command))

    private def wget(targetPath: String, remoteUrl: String, sudo: Boolean): String =
      execute(sudo, which("wget", sudo), Array("-q", "-O", targetPath, remoteUrl))


    def fileIsExist(targetPath: String, sudo: Boolean): Boolean =
      execute(sudo, s"[ -e $targetPath ] && echo true || echo false", Array.empty).toBoolean

    def downloadIfNotExist(targetDir: String, fileName: String, remoteUrl: String, sudo: Boolean): Boolean =
    //判断文件是否存在
      if (!fileIsExist(s"$targetDir/$fileName", sudo)) {
        //判断文件存放目录是否存在
        if (!fileIsExist(targetDir, sudo))
          mkdir(targetDir, sudo)
        //下载文件
        wget(s"$targetDir/$fileName", remoteUrl, sudo).nonEmpty
      }
      else true

    /**
     * copy file
     *
     * @param originalPath 文件原始目录
     * @param targetPath   文件目标目录
     * @param sudo         是否sudo
     * @return
     */
    def copy(originalPath: String, targetPath: String, sudo: Boolean): String =
      execute(sudo, which("cp", sudo), Array(originalPath, targetPath))

    /**
     *
     * @param originalPath 文件原始目录
     * @param targetPath   文件目标目录
     * @param sudo         是否sudo
     * @return
     */
    def copyAfterRemoveOld(originalPath: String, targetPath: String, sudo: Boolean): String = {
      // 删除旧的数据，如果有旧数据的话, 禁止以sudo模式删除
      rm(targetPath)
      // create directory
      mkdir(targetPath, sudo)
      copy(originalPath, targetPath, sudo)
    }

    def rm(filePath: String): String =
      execute(sudo = false, which("rm", sudo = false), Array("-rf", filePath))


    def unzipToTargetPath(filePath: String, targetPath: String, sudo: Boolean): String =
      execute(sudo, which("tar", sudo), Array("-xzf", filePath, "-C", targetPath))

    /**
     * mkdir -p
     *
     * @param path 路径
     * @param sudo 是否sudo
     * @return
     */
    def mkdir(path: String, sudo: Boolean): String =
      execute(sudo, which("mkdir", sudo), Array("-p", path))

    /**
     * 执行Shell脚本或命令
     *
     * @param sudo   是否sudo
     * @param comand 执行命令
     * @param args   参数
     * @return
     */
    def execute(sudo: Boolean, comand: String, args: Array[String]): String = {
      val session: Session = connection.openSession
      val completeCommand: String = s"${isSudo(sudo)} ${generateCommand(comand, args)}".trim
      log.debug("full execute command is {}", completeCommand)
      session.execCommand(completeCommand)
      val in: InputStream = session.getStdout
      val out: String = processStandardOutput(in)
      val errorIn: InputStream = session.getStderr
      val errorOut: String = processStandardOutput(errorIn)
      log.debug(completeCommand + " execute res = {}", out)
      log.debug(completeCommand + " execute error = {}", errorOut)
      in.close()
      errorIn.close()
      out
    }

    private def generateCommand(command: String, args: Array[String]): String =
      args.foldLeft(command)((command: String, arg: String) => s"$command $arg")

    /**
     * 解析流获取字符串信息
     *
     * @return
     */
    private def processStandardOutput(inputStream: InputStream): String = {
      @scala.annotation.tailrec
      def readLine(sb: StringBuffer, reader: BufferedReader): StringBuffer = {
        val line: String = reader.readLine()
        if (line != null) readLine(sb.append(line).append("\n"), reader)
        else sb
      }

      Try(new BufferedReader(new InputStreamReader(inputStream)))
        .map((reader: BufferedReader) => readLine(new StringBuffer(), reader).toString) match {
        case Failure(exception) => throw exception
        case Success(value) => value.replace("\n", "")
      }
    }

    def close(): Unit = connection.close()
  }

}



