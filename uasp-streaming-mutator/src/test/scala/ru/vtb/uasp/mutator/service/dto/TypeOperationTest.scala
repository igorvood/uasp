package ru.vtb.uasp.mutator.service.dto

import org.scalatest.flatspec.AnyFlatSpec

class TypeOperationTest extends AnyFlatSpec {

  val key = "someStringKey"
  val value = "someStringValue"


  behavior of "Add Operation"

  it should "exception on mutate String with contains value in Map" in {
    addThrows("someString", "someString")
  }

  it should "exception on mutate Int with contains value in Map" in {
    val v: Int = 9
    addThrows("someString", v)
  }

  it should "exception on mutate Long with contains value in Map" in {
    val v: Long = 9
    addThrows("someString", v)
  }

  it should "exception on mutate String with null as new value" in {
    val v: String = null
    addThrows("someString", v)
  }

  it should "no exception on mutate String with not null as new value" in {
    val notMutatedMap = Map(key + 1 -> value)
    val newVal = value + "11111"
    val resultMap = Add().mutate(notMutatedMap, Some(newVal), key)
    val actualVal = resultMap(key)
    assertResult(newVal)(actualVal)
  }

  private def addThrows[T](key: String, value: T) = {
    assertThrows[IllegalStateException](Add().mutate(Map(key -> value), Some(value), key))
  }

  it should "exception on delete wrong value" in {
    //add default map to another tests
    val notMutatedMap = Map(key + 1 -> value)
    println(notMutatedMap.toString())
    val newVal = value + "11111"
    assertThrows[IllegalStateException](Delete().mutate(notMutatedMap, Some(newVal), key))
  }

  behavior of "Delete Operation"

  it should "delete correct value is Ok" in {
    val notMutatedMap = Map(key -> value)
    val mutatedMap = Delete().mutate(notMutatedMap, Some(value), key)
    assert(!mutatedMap.contains(key))
  }

  behavior of "Mutate Operation"

  it should "exception on mutate wrong key" in {
    val notMutatedMap = Map(key + 1 -> value)
    val newVal = value + "11111"
    assertThrows[IllegalStateException](Mutate().mutate(notMutatedMap, Some(newVal), key))
  }

  it should "exception on mutate None value" in {
    val notMutatedMap = Map(key + 1 -> value)
    assertThrows[IllegalStateException](Mutate().mutate(notMutatedMap, None, key))
  }

  it should "mutate correct value is Ok" in {
    val notMutatedMap = Map(key -> value)
    val newVal = value + "11111"
    val mutatedMap = Mutate().mutate(notMutatedMap, Some(newVal), key)
    //May be another way?
    assertResult(mutatedMap.get(key))(Some(newVal))
  }

  behavior of "AddOrMutate Operation"

  it should "success add key" in {
    val notMutatedMap = Map(key + 1 -> value)
    val newVal = value + "11111"
    val mutatedMap = AddOrMutate().mutate(notMutatedMap, Some(newVal), key)
    assertResult(mutatedMap.get(key))(Some(newVal))

  }

  it should "success mutate key" in {
    val notMutatedMap = Map(key -> value)
    val newVal = value + "11111"
    val mutatedMap = AddOrMutate().mutate(notMutatedMap, Some(newVal), key)
    assertResult(mutatedMap.get(key))(Some(newVal))
  }

  it should "error add null value" in {
    val notMutatedMap = Map(key + 1 -> value)
    assertThrows[IllegalStateException](AddOrMutate().mutate(notMutatedMap, None, key))
  }

  it should "error mutate null value" in {
    val notMutatedMap = Map(key -> value)
    assertThrows[IllegalStateException](AddOrMutate().mutate(notMutatedMap, None, key))

  }



}
