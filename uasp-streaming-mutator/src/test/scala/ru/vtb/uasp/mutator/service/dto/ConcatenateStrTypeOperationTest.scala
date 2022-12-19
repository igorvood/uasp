package ru.vtb.uasp.mutator.service.dto

import org.scalatest.flatspec.AnyFlatSpec

class ConcatenateStrTypeOperationTest extends AnyFlatSpec {

  val key = "someStringKey"
  val value = "someStringValue"


  behavior of "ConcatenateStr Operation"

  it should "  on ConcatenateStr None value must throw exception " in {
    assertThrows[IllegalStateException](ConcatenateStr().mutate(Map[String, String](), None, key))
  }

  it should "  on ConcatenateStr first value concat with empty string " in {
    val newMap = ConcatenateStr(",").mutate(Map[String, String](), Some(value), key)
    assert(newMap.contains(key))
    assertResult(value)(newMap(key))
  }

  it should "  on ConcatenateStr second value concat with old string, adding to tail" in {
    val newMap = ConcatenateStr(",").mutate(Map[String, String](key -> s"${value}Other"), Some(value), key)
    assert(newMap.contains(key))
    val str = newMap(key)
    assertResult(s"${value}Other,$value")(str)
  }

  it should "  on ConcatenateStr Some with not String value must throw exception " in {
    assertThrows[IllegalStateException](ConcatenateStr().mutate(Map[String, String](), Some(1), key))
  }

  it should "  on ConcatenateStr Some with not String value must throw exception and map not sting" in {
    assertThrows[IllegalStateException](ConcatenateStr().mutate(Map[String, Int](), Some(1), key))
  }


}
