package cn.lancedai.weye.common.tool

import java.io.File

import scala.util.{Success, Try}

object FileTool {
  def checkFileExist(filePath: String): Boolean = {
    import scala.util.Failure
    Try {
      new File(filePath)
    } match {
      case Success(_) => true
      case Failure(exception) => exception.printStackTrace(); false
    }
  }
}
