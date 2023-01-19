package ru.vtb.uasp.vector.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import ru.vtb.uasp.common.dto.UaspDto

class UaspUtilTest extends AnyFlatSpec {
  "UaspUtil.getSystemName " should "be equals PROFILE" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_prf" -> "Profile"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "PROFILE"
  }

  "UaspUtil.getSystemName " should "be not equals PROFILE" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_prf" -> "Not Profile"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp) shouldBe None
  }

  "UaspUtil.getSystemName " should "be equals WAY4" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_w4" -> "WAY4"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "WAY4"
  }

  "UaspUtil.getSystemName " should "be not equals WAY4" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_w4" -> "NOT WAY4"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp) shouldBe None
  }

  "UaspUtil.getSystemName " should "be equals CFT" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_cft" -> "CFT2RS"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "CFT"
  }

  "UaspUtil.getSystemName " should "be not equals CFT" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("source_system_cft" -> "Not CFT2RS"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp) shouldBe None
  }

  "UaspUtil.getSystemName " should "be equals WITHDRAW" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("is_withdraw" -> true),
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "WITHDRAW"
  }

  "UaspUtil.getSystemName " should "be not equals WITHDRAW" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("is_withdraw" -> false),
      "",
      0L)

    UaspUtil.getSystemName(uasp) shouldBe None
  }

  "UaspUtil.getSystemName " should "be equals SOME" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("system_source" -> "SOME_SYSTEM"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "SOME_SYSTEM"
  }

  "UaspUtil.getSystemName " should "be equals UDDS" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map("system_source" -> "Udds"),
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp).get shouldBe "UDDS"
  }

  "UaspUtil.getSystemName " should "be equals NONE" in {
    val uasp = UaspDto(
      "test",
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      Map.empty,
      "",
      0L)

    UaspUtil.getSystemName(uasp) shouldBe None
  }

}
