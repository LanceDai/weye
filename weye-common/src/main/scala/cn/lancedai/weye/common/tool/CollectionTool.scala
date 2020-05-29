package cn.lancedai.weye.common.tool

import java.lang.reflect.Field

import cn.lancedai.weye.common.JMap

import scala.util.{Failure, Try}

object CollectionTool {


  def mapToObject[T](map: JMap[String, Any], `class`: Class[T]): T = {
    val obj: T = `class`.getDeclaredConstructor().newInstance()
    `class`.getDeclaredFields.foreach((field: Field) => {
      import scala.util.Success
      field.setAccessible(true)
      val fieldName: String = field.getName
      val res: Try[Unit] = Option(map.get(fieldName)) match {
        case Some(v) => Try(field.set(obj, v))
        case None => Failure(new Exception(s"no value found: $fieldName"))
      }
      res match {
        case Failure(exception) => exception.printStackTrace()
        case Success(value) =>
      }
    })
    obj
  }

  implicit class PlusMap[K, V](map: JMap[K, V]) {
    def removeWithException(key: K): Try[V] = {
      Try(map.remove(key)).map(Option(_: V)).map {
        case Some(value) => value
        case None => throw new Exception("相关源未在Agent中")
      }
    }
  }

}

