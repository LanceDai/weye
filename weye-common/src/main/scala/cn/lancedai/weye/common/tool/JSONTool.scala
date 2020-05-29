package cn.lancedai.weye.common.tool

import com.alibaba.fastjson.serializer.SerializeConfig

object JSONTool {


  private val conf = new SerializeConfig(true)

  import cn.lancedai.weye.common.JHashMap
  import com.alibaba.fastjson.JSON

  def jsonStrToMap(jsonStr: String): JHashMap[String, Any] = {
    JSON.parseObject(jsonStr, classOf[JHashMap[String, Any]])
  }

  def objectToJsonStr(any: Any): String = JSON.toJSONString(any, conf)

  def objectToMap(any: Any): JHashMap[String, Any] = jsonStrToMap(objectToJsonStr(any))

  def jsonToObject[T](jsonStr: String, `class`: Class[T]): T =
    JSON.parseObject(jsonStr, `class`)

}
