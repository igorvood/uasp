package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object Way4UaspDtoDao {


  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "WAY4"

    // val test: String = (inMessage \ "test").extract[String]
    // DKO WAY4
    lazy val operationId: String = (inMessage \ "id").extract[String] // нужно ли сериализовать
    lazy val cardClientId: String = (inMessage \ "account" \ "client" \ "id").extract[String] // у нас id это id клиента, правильно ли это?
    lazy val auditRefAuthcode: String = (inMessage \ "chain" \ "ref" \ "authCode").extract[String]
    lazy val auditRefRrn: String = (inMessage \ "chain" \ "ref" \ "rrn").extract[String]
    lazy val auditRefSrn: String = (inMessage \ "chain" \ "ref" \ "srn").extract[String]
    lazy val serviceType: String = (inMessage \ "chain" \ "serviceType").extract[String]
    lazy val paymentScheme: String = (inMessage \ "card" \ "cardType" \ "paymentScheme").extract[String]
    lazy val expire: String = (inMessage \ "card" \ "plastic" \ "expire").extract[String]
    lazy val actionType: String = (inMessage \ "actionType").extract[String]
    lazy val transactionAmount: BigDecimal = BigDecimal((inMessage \ "requestedAmount" \ "transaction" \ "amount").extract[String])
    lazy val transactionDatetime: Long = dtStringToLong((inMessage \ "chain" \ "serviceDateTime").extract[String], "yyyy-MM-dd'T'HH:mm:ss", "GMT+0000")//TODO must be MSK
    lazy val processingDatetime: Long = dtStringToLong((inMessage \ "processing" \ "processedAt").extract[String], "yyyy-MM-dd'T'HH:mm:ss","GMT+0000")
    lazy val effectiveDate: Long = dtStringToLong((inMessage \ "processing" \ "effectiveDate").extract[String], "yyyy-MM-dd", "GMT+0000") // проверить чтобы были нули в времени
    lazy val processingResolution: String = (inMessage \ "processing" \ "resolution").extract[String]
    lazy val paymentDirection: String = (inMessage \ "requestedAmount" \ "paymentDirection").extract[String]
    lazy val processingResultCode: String = (inMessage \ "processing" \ "resultCode").extract[String] // какой тип сделать int или string
    lazy val mcc: String = (inMessage \ "pointOfService" \ "mcc").extract[String]
    lazy val terminalType: String = (inMessage \ "pointOfService" \ "terminalType").extract[String]
    lazy val merchantName: String = (inMessage \ "pointOfService" \ "merchantName").extract[String]
    lazy val terminalId : String = (inMessage \ "pointOfService" \ "terminalId").extract[String]
    lazy val processedAtInString = (inMessage \ "processing" \ "processedAt").extract[String]
    //POS transaction   https://wiki.corp.dev.vtb/pages/viewpage.action?pageId=1760826766

    lazy val transactionCurrency: String = (inMessage \ "requestedAmount" \ "transaction" \ "currency").extract[String]
    lazy val cardMaskedPan: String = (inMessage \ "card" \ "maskedPan").extract[String]
    lazy val cardPsFundingSource: String = (inMessage \ "card" \ "cardType" \ "psFundingSource").extract[String]

    // WAY4 first salary https://wiki.corp.dev.vtb/pages/viewpage.action?pageId=1562230879
    lazy val chainServiceTypeExtension: String = (inMessage \ "chain" \ "serviceTypeExtension").extract[String]
    lazy val accountNumber: String = (inMessage \ "account" \ "accountNumber").extract[String]
    lazy val proccesingBaseAmount: BigDecimal = BigDecimal((inMessage \ "processing" \ "baseAmount" \ "amount").extract[String])
    lazy val proccesingBaseAmountCurrency: String = (inMessage \ "processing" \ "baseAmount" \ "currency").extract[String]

    lazy val proccesingFeeAmount: BigDecimal = BigDecimal((inMessage \ "processing" \ "feeAmount" \ "amount").extract[String])
    lazy val proccesingFeeAmountCurrency: String = (inMessage \ "processing" \ "feeAmount" \ "currency").extract[String]

    lazy val counterpartyAccountNumber: String = (inMessage \ "counterpartyPaymentDetails" \ "party" \ "accountInfo" \ "accountNumber").extract[String]

    lazy val taggedDataKDO: String = (inMessage \ "taggedData" \ "KBO").extract[String]
    lazy val taggedDataSourcePay: String = (inMessage \ "taggedData" \ "SORCE_PAY").extract[String]
    lazy val taggedDataWalletPay: String = (inMessage \ "taggedData" \ "WALLET_TYPE").extract[String]
//    lazy val comment: String = (inMessage \ "comment").extract[String]


    val dataInt = Map[String, Int]()
    //    val dataInt = Map[String, Int]() ++
    //      getMap[Int](dtoMap("app.uaspdto.fields.processing_result_code")(0), processingResultCode)
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.transaction_datetime")(0), transactionDatetime) ++
      getMap[Long](dtoMap("app.uaspdto.fields.processing_datetime")(0), processingDatetime) ++
      getMap[Long](dtoMap("app.uaspdto.fields.effective_date")(0), effectiveDate)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.transaction_amount")(0), transactionAmount) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.baseamount_amount")(0), proccesingBaseAmount) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.feeamount_amount")(0), proccesingFeeAmount)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.source_system")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.operation_id")(0), operationId) ++
      getMap[String](dtoMap("app.uaspdto.fields.local_user_id")(0), cardClientId) ++ // нафига так назвали?
      getMap[String](dtoMap("app.uaspdto.fields.audit_ref_authcode")(0), auditRefAuthcode) ++
      getMap[String](dtoMap("app.uaspdto.fields.audit_ref_rrn")(0), auditRefRrn) ++
      getMap[String](dtoMap("app.uaspdto.fields.audit_ref_srn")(0), auditRefSrn) ++
      getMap[String](dtoMap("app.uaspdto.fields.action_type")(0), actionType) ++
      getMap[String](dtoMap("app.uaspdto.fields.service_type")(0), serviceType) ++
      getMap[String](dtoMap("app.uaspdto.fields.processing_resolution")(0), processingResolution) ++
      getMap[String](dtoMap("app.uaspdto.fields.payment_direction")(0), paymentDirection) ++
      getMap[String](dtoMap("app.uaspdto.fields.processing_result_code")(0), processingResultCode) ++
      getMap[String](dtoMap("app.uaspdto.fields.mcc")(0), mcc) ++
      getMap[String](dtoMap("app.uaspdto.fields.terminal_type")(0), terminalType) ++
      getMap[String](dtoMap("app.uaspdto.fields.merchant_name")(0), merchantName) ++
      getMap[String](dtoMap("app.uaspdto.fields.transaction_currency")(0), transactionCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.card_masked_pan")(0), cardMaskedPan) ++
      getMap[String](dtoMap("app.uaspdto.fields.card_ps_funding_source")(0), cardPsFundingSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.chain_serviceTypeExtension")(0), chainServiceTypeExtension) ++
      getMap[String](dtoMap("app.uaspdto.fields.account_number")(0), accountNumber) ++
      getMap[String](dtoMap("app.uaspdto.fields.baseAmount_currency")(0), proccesingBaseAmountCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.feeAmount_currency")(0), proccesingFeeAmountCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.taggedData.KBO")(0), taggedDataKDO) ++
      getMap[String](dtoMap("app.uaspdto.fields.card.cardType.paymentScheme")(0), paymentScheme) ++
      getMap[String](dtoMap("app.uaspdto.fields.card.plastic.expire")(0), expire) ++
      getMap[String](dtoMap("app.uaspdto.fields.taggedData.SORCE_PAY")(0), taggedDataSourcePay) ++
      getMap[String](dtoMap("app.uaspdto.fields.processing_date_string")(0), processedAtInString) ++
      getMap[String](dtoMap("app.uaspdto.fields.terminal_id")(0), terminalId) ++
      getMap[String](dtoMap("app.uaspdto.fields.counterpartyAccountNumber")(0), counterpartyAccountNumber) ++
      getMap[String](dtoMap("app.uaspdto.fields.taggedData.WALLET_TYPE")(0), taggedDataWalletPay)
//      getMap[String](dtoMap("app.uaspdto.comment")(0), comment)


    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = cardClientId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = dataInt,
      dataLong = dataLong,
      dataFloat = dataFloat,
      dataDouble = dataDouble,
      dataDecimal = dataDecimal,
      dataString = dataString,
      dataBoolean = dataBoolean
    )
  }
}
