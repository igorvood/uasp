package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, getMapO, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.Way4UaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object Way4UaspDtoDao {

  lazy val systemSource = "WAY4"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value1 = inMessage.validate[Way4UaspDto].map(dj => {
      UaspDto(
        id = dj.account.flatMap(_.client.flatMap(_.id)).orNull,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map.empty ++ getMapO[Long](dtoMap("app.uaspdto.fields.transaction_datetime")(0), dj.chain.flatMap(c => c.serviceDateTime)) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.processing_datetime")(0), dj.processing.flatMap(p => p.processedAt)) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.effective_date")(0), dj.processing.flatMap(p => p.effectiveDate))
        ,
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.transaction_amount")(0), dj.requestedAmount.flatMap(r => r.transaction.flatMap(t => t.amount)).orNull),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.baseamount_amount")(0), dj.processing.flatMap(p => p.baseAmount.flatMap(b => b.amount)).orNull),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.feeamount_amount")(0), dj.processing.flatMap(p => p.feeAmount.flatMap(f => f.amount)).orNull)
        )
        ,
        dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.source_system")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.operation_id")(0), dj.id.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.local_user_id")(0), dj.account.flatMap(a => a.client.flatMap(c => c.id)).orNull), // нафига так назвали?
          getMapEntry[String](dtoMap("app.uaspdto.fields.account_number")(0), dj.account.flatMap(a => a.accountNumber).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.service_type")(0), dj.chain.flatMap(c => c.serviceType).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.audit_ref_authcode")(0), dj.chain.flatMap(c => c.ref.flatMap(r => r.authCode)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.audit_ref_rrn")(0), dj.chain.flatMap(c => c.ref.flatMap(r => r.rrn)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.audit_ref_srn")(0), dj.chain.flatMap(c => c.ref.flatMap(r => r.srn)).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.action_type")(0), dj.actionType.orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.processing_resolution")(0), dj.processing.flatMap(p => p.resolution).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.processing_result_code")(0), dj.processing.flatMap(p => p.resultCode).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.payment_direction")(0), dj.requestedAmount.flatMap(r => r.paymentDirection).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.transaction_currency")(0), dj.requestedAmount.flatMap(r => r.transaction.flatMap(t => t.currency)).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.mcc")(0), dj.pointOfService.flatMap(p => p.mcc).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.terminal_type")(0), dj.pointOfService.flatMap(p => p.terminalType).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.merchant_name")(0), dj.pointOfService.flatMap(p => p.merchantName).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.card_masked_pan")(0), dj.card.flatMap(c => c.maskedPan).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.card_ps_funding_source")(0), dj.card.flatMap(c => c.cardType.flatMap(ct => ct.psFundingSource)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.chain_serviceTypeExtension")(0), dj.chain.flatMap(c => c.serviceTypeExtension).orNull),

          getMapEntry[String](dtoMap("app.uaspdto.fields.baseAmount_currency")(0), dj.processing.flatMap(p => p.baseAmount.flatMap(b => b.currency)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.feeAmount_currency")(0), dj.processing.flatMap(_.feeAmount.flatMap(_.currency)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.taggedData.KBO")(0), dj.taggedData.flatMap(_.KBO).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.taggedData.orig_src_code")(0), dj.taggedData.flatMap(_.ORIG_SRC_CODE).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.card.cardType.paymentScheme")(0), dj.card.flatMap(_.cardType.flatMap(_.paymentScheme)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.card.plastic.expire")(0), dj.card.flatMap(_.plastic.flatMap(_.expire)).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.taggedData.SORCE_PAY")(0), dj.taggedData.flatMap(_.SOURCE_PAY).orNull),
          //          getMapEntry[String](dtoMap("app.uaspdto.fields.processing_date_string")(0), dj.processing.processedAt.toString) , //2022-06-21T13:21:43Z
          getMapEntry[String](dtoMap("app.uaspdto.fields.terminal_id")(0), dj.pointOfService.flatMap(_.terminalId).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.counterpartyAccountNumber")(0), dj.counterpartyPaymentDetails.flatMap(_.party.flatMap(_.accountInfo.flatMap(_.accountNumber))).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.taggedData.WALLET_TYPE")(0), dj.taggedData.flatMap(_.WALLET_TYPE).orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.comment")(0), dj.comment.orNull)

        ),
        dataBoolean = Map.empty
      )
    }
    ).map(d => Json.toJson(d))

    List(value1)

  }
}
