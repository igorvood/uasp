package ru.vtb.uasp.common.state.mock

import org.apache.flink.api.common.state.MapState

import java.util.Map
import java.{lang, util}


class MapStateMock[A,B] extends MapState[A,B] {
  val map: util.HashMap[A,B] = new util.HashMap[A,B]()

  override def get(uk: A): B = map.get(uk)

  override def put(uk: A, uv: B): Unit = map.put(uk, uv)

  override def putAll(map: util.Map[A, B]): Unit = map.putAll(map)

  override def remove(uk: A): Unit = map.remove(uk)

  override def contains(uk: A): Boolean = map.containsKey(uk)

  override def entries(): lang.Iterable[Map.Entry[A, B]] = map.entrySet()

  override def keys(): lang.Iterable[A] = map.keySet()

  override def values(): lang.Iterable[B] = map.values

  override def iterator(): util.Iterator[Map.Entry[A, B]] = map.entrySet().iterator()

  override def isEmpty: Boolean = map.isEmpty

  override def clear(): Unit = map.clear()
}
