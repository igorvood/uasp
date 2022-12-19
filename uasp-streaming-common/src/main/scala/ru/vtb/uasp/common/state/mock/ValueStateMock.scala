package ru.vtb.uasp.common.state.mock

import org.apache.flink.api.common.state.ValueState

class ValueStateMock[T](var valueState: T) extends ValueState[T] {

  override def value(): T = valueState

  override def update(t: T): Unit = { valueState = t }

  override def clear(): Unit = { valueState = null.asInstanceOf[T] }
}
