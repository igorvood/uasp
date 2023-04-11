package ru.vtb.uasp.streaming.mdm.enrichment.itest.dao

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.FooCounter
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendRateScenarioBuilder
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendRateScenarioBuilder.timeNow

import java.text.SimpleDateFormat
import java.util.Calendar

object DataGeneratorDao {


  def generateMsgFields(id: Int): Map[String, Any] = {
    val random = new scala.util.Random
    val fooCounter = new FooCounter(id)
    val USER_START_ID_NUMBER = (Math.random() * 100000).toInt * 10000 + fooCounter.inc() * random.nextInt(1000)

    Map(
      "transaction_datetime" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Calendar.getInstance().getTime),
      "processing_datetime" -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Calendar.getInstance().getTime),
      "processing_effectivedate" -> new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime),
      "op_id" -> random.nextInt(999999),
      "audit_ref_authcode" -> random.nextInt(999999),
      "audit_ref_rrn" -> random.alphanumeric.take(12).mkString.toUpperCase,
      "audit_ref_srn" -> random.alphanumeric.take(12).mkString.toUpperCase,
      "local_user_id" -> (fooCounter.inc() + USER_START_ID_NUMBER).toString,
      "balance_change" -> random.nextInt(9999)
    )
  }

  def generateWay4(userIdLocal: String, idMsg: String): UaspDto = {
    val random = new scala.util.Random
    UaspDto(
      id = userIdLocal,
      dataInt = Map(
        "audit_auth_code" -> random.nextInt(999999),
        "balance_change" -> random.nextInt(9999),
        "op_id" -> random.nextInt(999999),
      ),
      dataLong = Map(
        "transaction_datetime" -> 1626621144000L,
        "processing_datetime" -> 1626631944000L,
        "effective_date" -> 1626555600000L,
        "currency_date" -> timeNow.plusDays(1).toLocalDate.toDate.getTime
      ),
      dataFloat = Map(),
      dataDouble = Map(),
      dataDecimal = Map("transaction_amount" -> BigDecimal(6651.78)),
      dataString = Map(
        "local_id" -> userIdLocal,
        "audit_rrn" -> random.alphanumeric.take(12).mkString.toUpperCase,
        "audit_srn" -> random.alphanumeric.take(12).mkString.toUpperCase,

        "processing_result_code" -> "0",

        "action_type" -> "Presentment",
        "audit_auth_code" -> "3687",
        "processing_resolution" -> "Accepted",
        "action_type" -> "Presentment",
        "payment_direction" -> "Debit",
        "idMsg" -> idMsg,
        "rates_currency_alphaCode" -> SendRateScenarioBuilder.rateList(userIdLocal.hashCode.abs % SendRateScenarioBuilder.rateList.size)
      ),
      dataBoolean = Map(),
      uuid = java.util.UUID.randomUUID().toString,
      process_timestamp = Calendar.getInstance().getTimeInMillis
    )
  }


  def generateCrossLinkMdm(userIdLocal: String, idGloabal: String): UaspDto = {
    UaspDto(
      userIdLocal,
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map("local_id" -> userIdLocal, "global_id" -> idGloabal),
      Map(),
      java.util.UUID.randomUUID().toString,
      Calendar.getInstance().getTimeInMillis
    )
  }

  def generateMortgageMdm(idGloabal: String): UaspDto = {
    UaspDto(
      idGloabal,
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map("is_mortgage" -> (idGloabal.map(a => a.toInt).sum % 2 == 0)),
      java.util.UUID.randomUUID().toString,
      Calendar.getInstance().getTimeInMillis
    )
  }

}
