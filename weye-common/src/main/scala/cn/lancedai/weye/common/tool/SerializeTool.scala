package cn.lancedai.weye.common.tool

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object SerializeTool {

  implicit def tryExtractor[T >: Null](tryUnit: Try[T]): T = tryUnit match {
    case Failure(exception) =>
      exception.printStackTrace()
      null
    case Success(value) => value
  }

  def serialize(obj: Any): Try[Array[Byte]] = {
    for {
      baos <- Try(new ByteArrayOutputStream)
      oos <- Try(new ObjectOutputStream(baos))
    } yield {
      oos.writeObject(obj)
      baos.toByteArray
    }
  }

  def deserialize[T](bytes: Array[Byte], className: Class[T]): Try[T] = {
    for {
      bais <- Try(new ByteArrayInputStream(bytes))
      ois <- Try(new ObjectInputStream(bais))
      obj <- Try(ois.readObject.asInstanceOf[T])
    } yield obj
  }
}
