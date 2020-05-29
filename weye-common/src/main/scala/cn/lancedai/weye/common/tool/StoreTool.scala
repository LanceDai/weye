package cn.lancedai.weye.common.tool

/**
 * 这是一个简易的贮藏者模式， 用来模拟redis
 */
object StoreTool {

  import java.time.Duration
  import java.util.Optional
  import java.util.concurrent.ConcurrentHashMap

  case class ValueTime(value: String, time: Long)

  private val store = new ConcurrentHashMap[String, ValueTime]()


  def set(key: String, value: String, duration: Duration): Unit = {
    store.put(key, ValueTime(value, System.currentTimeMillis() + duration.toMillis))
  }

  def get(key: String): Optional[String] = {
    this.synchronized {
      val res: ValueTime = store.get(key)
      if (res == null) Optional.empty()
      else if (res.time < System.currentTimeMillis()) {
        clear(key)
        Optional.empty()
      } else Optional.of(res.value)
    }
  }

  def clear(key: String): Unit = store.remove(key)
}
