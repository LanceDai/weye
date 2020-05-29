package cn.lancedai.weye.common.tool

import java.security.MessageDigest
import java.util.UUID

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.sys.process._
import scala.util.matching.Regex

object StringTool {


  /** 驼峰转下划线 */
  //  def humpToLine(str: String): String = "[A-Z]".r.replaceAllIn(str, "_$0").toUpperCase
  def humpToLine(str: String): String = humpToChar(str, "_")

  /**
   * 驼峰转任意字符
   *
   * @param str 原始字符串
   * @param c   待添加字符
   * @return
   */
  def humpToChar(str: String, c: String): String = {
    val res: String = "[A-Z]".r.replaceAllIn(str, c + "$0").toUpperCase
    // 去除首位影响
    if (res.startsWith(c)) res.substring(1, res.length)
    else res
  }

  /** 下划线转驼峰 */
  def lineToHump(_str: String): String = "_\\w".r.replaceSomeIn(_str, (m: Regex.Match) =>
    Option(m.matched.replace("_", "").toUpperCase)
  )

  def trimEachEndChars(srcStr: String, splitter: String): String =
    trimStartEndChars(srcStr, splitter, splitter)

  def trimStartChars(srcStr: String, splitter: String): String =
    trimStartEndChars(srcStr, splitter, "")

  def trimEndChars(srcStr: String, splitter: String): String =
    trimStartEndChars(srcStr, "", splitter)

  def trimStartEndChars(srcStr: String, startSplitter: String, endSplitter: String): String = {
    val startIdx: Int = if (startSplitter.nonEmpty && srcStr.startsWith(startSplitter)) 1 else 0
    val endIdx: Int = if (endSplitter.nonEmpty && srcStr.endsWith(endSplitter)) srcStr.length - 1 else srcStr.length
    srcStr.substring(startIdx, endIdx)
  }


  lazy val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")

  def encode(content: String): String = {
    //二进制转字符串
    def byte2hex(b: Array[Byte]): String =
      b.foldLeft(new StringBuffer())((sb: StringBuffer, byte: Byte) => {
        val stmp: String = Integer.toHexString(byte & 0XFF)
        sb.append(if (stmp.length == 1) s"0$stmp"

        else stmp
        )
      }

      ).toString

    byte2hex(messageDigest.digest(content.getBytes))
  }

  def generateRandomValue(): String = {
    UUID.randomUUID().toString
  }

  implicit def stringToCommand(command: String): ProcessBuilder = {
    def resolve(command: String, regex: String)
               (op: String => ProcessBuilder)
               (op2: (ProcessBuilder, ProcessBuilder) => ProcessBuilder)
    : ProcessBuilder = command match {
      case _ if command.nonEmpty =>
        val array: Array[String] = command.split(regex)
          .filter((_: String) != regex)
          .map((_: String).trim)
        val first: String = array(0)
        array.tail.foldLeft(op(first)) {
          (p: ProcessBuilder, str: String) => op2(p, op(str))
        }
    }

    def stringToSeq(str: String): Seq[String] = {
      val stringBuilder: StringBuilder = new StringBuilder()
      val resList: ListBuffer[String] = new mutable.ListBuffer[String]()
      var flag: Boolean = false
      var flag2: Boolean = false
      for (c <- str.toCharArray) {
        c match {
          case '{' => flag2 = true
            stringBuilder.append('{')
          case '}' => flag2 = false
            stringBuilder.append('}')
          case x =>
            if (flag2) stringBuilder.append(x)
            else x match {
              case ' ' if !flag => resList += stringBuilder.toString()
                stringBuilder.clear()
              case '"' | '\'' => flag = !flag
              case x => stringBuilder.append(x)
            }
        }

      }
      if (stringBuilder.nonEmpty) resList += stringBuilder.toString()
      resList.toList
    }

    resolve(command, "\\|\\|") {
      resolve(_: String, "&&") {
        resolve(_: String, "\\|") {
          stringToSeq
        }((_: ProcessBuilder) #| (_: ProcessBuilder))
      }((_: ProcessBuilder) #&& (_: ProcessBuilder))
    }((_: ProcessBuilder) #|| (_: ProcessBuilder))
  }

  def repeatChar(char: Char, num: Int): String =
    (1 to num).foldRight(new StringBuffer())((_: Int, sb: StringBuffer) => sb.append(char)).toString

  private val byteUnit: Array[String] = Array(
    "Byte",
    "KB",
    "MB",
    "GB",
    "TB"
  )

  def normalizedByte(bytes: Long): String = {
    @scala.annotation.tailrec
    def go(bytes: Double, count: Int): String = {
      val remainder: Double = bytes / 1024;
      if (remainder <= 1) s"${"%.2f".format(bytes)}${byteUnit(count)}"
      else go(remainder, count + 1)
    }

    go(bytes, 0)
  }

  //首字母大写
  def upperFirstChar(str: String): String = s"${str.head.toUpper}${str.tail}"
}