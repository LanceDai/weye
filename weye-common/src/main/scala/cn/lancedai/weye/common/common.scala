package cn.lancedai.weye

package object common {
  type JHashMap[K, V] = java.util.HashMap[K, V]
  type JMap[K, V] = java.util.Map[K, V]
  type JSet[E] = java.util.Set[E]
  type JSerializable = java.io.Serializable
}
