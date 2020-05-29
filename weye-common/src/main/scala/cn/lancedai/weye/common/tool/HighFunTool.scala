package cn.lancedai.weye.common.tool

import scala.language.reflectiveCalls

object HighFunTool {

  case class Closable[A <: {def close(): Unit}](a: A) {

    def map[B](f: A => B): B =
      try f(a)
      finally {
        if (a != null) a.close()
      }

    def flatMap[B](f: A => B): B = map(f)

  }

}
