package ru.vtb.uasp.mdm.enrichment.test.utils

import ru.vtb.uasp.common.dto.UaspDto

import java.util.Calendar

object DataGenerateUtil {

  def getEmptyWay4UaspDto: UaspDto =
    UaspDto(
      "1",
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      java.util.UUID.randomUUID().toString,
      Calendar.getInstance().getTimeInMillis
    )

  def getWay4UaspDto: UaspDto = {
    val random = new scala.util.Random
    UaspDto(
      "",
      Map(
        "audit_auth_code" -> random.nextInt(999999),
        "balance_change" -> random.nextInt(9999),
        "op_id" -> random.nextInt(999999)
      ),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(
        "local_id" -> random.nextInt(9999).toString,
        "audit_rrn" -> random.alphanumeric.take(12).mkString.toUpperCase,
        "audit_srn" -> random.alphanumeric.take(12).mkString.toUpperCase),
      Map(),
      java.util.UUID.randomUUID().toString,
      Calendar.getInstance().getTimeInMillis
    )
  }

  def generateCrossLinkMdm(userIdLocal: String, userIdGlobal: String): UaspDto = {
    UaspDto(
      userIdGlobal,
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map("local_id" -> userIdLocal, "global_id" -> userIdGlobal),
      Map(),
      java.util.UUID.randomUUID().toString,
      Calendar.getInstance().getTimeInMillis
    )
  }


}
