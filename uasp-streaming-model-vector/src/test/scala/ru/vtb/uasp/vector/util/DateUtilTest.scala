package ru.vtb.uasp.vector.util

import org.scalatest.flatspec.AnyFlatSpec

class DateUtilTest extends AnyFlatSpec {

  "DateUtilTest.longToString " should "be converted to string with eventFormatter" in {
    assertResult("2022-10-25 05:52:25")(DateUtil.longToString(1666666345678L, DateUtil.eventFormatter))
  }

  "DateUtilTest.longToString " should "be converted to string with eventFormatter and timezone" in {
    assertResult("2022-10-25 08:52:25")(DateUtil.longToString(1666666345678L, DateUtil.eventFormatter, "Asia/Omsk"))
  }

  "DateUtilTest.longToString " should "be converted to string with kafkaFormatter" in {
    assertResult("2023-06-13 14:39:05.421")(DateUtil.longToString(1686656345421L, DateUtil.kafkaFormatter))
  }
}
