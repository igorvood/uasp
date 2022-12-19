package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class KeySelectorPropTest extends AnyFlatSpec with Matchers {

  it should "успешное создание настройки в разрезе id" in {
    KeySelectorProp(true, None, None)
  }

  it should "успешное создание настройки в разрезе поля из мапки" in {
    KeySelectorProp(false, Some("STRING"), Some("1"))
  }


  it should "Не успешное создание настройки не правильный тип мапки" in {
    assertThrows[IllegalArgumentException](KeySelectorProp(false, Some("1"), Some("1")))
  }

  it should "Не успешное создание настройки в разрезе поля из мапки, не указано значение ключа" in {
    assertThrows[IllegalArgumentException](KeySelectorProp(false, Some("STRING"), None))
  }


  it should "Не успешное создание настройки не одновеременная группировка и по id и по полю из мапки" in {
    assertThrows[IllegalArgumentException](KeySelectorProp(true, Some("1"), Some("1")))
  }


}
