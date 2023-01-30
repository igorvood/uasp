package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object FirstSalaryUaspDtoDao {
  def fromJValue(inMessage: JValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "CFT2RS"

    lazy val internalId: String = (inMessage \ "internalId").extract[String]
    lazy val mdmId = (inMessage \ "clientId").extract[String]
    lazy val eventTime = dtStringToLong((inMessage \ "eventTime").extract[String], "yyyy-MM-dd'T'HH:mm:ss", "Europe/Moscow")
    lazy val operationAmount = BigDecimal((inMessage \ "data" \ "operationAmount" \ "sum").extract[String])
    lazy val dataKBO = (inMessage \ "data" \ "KBO").extract[String]
    lazy val dataAccount = (inMessage \ "data" \ "account").extract[String]
    lazy val systemId = (inMessage \ "systemId").extract[String]
    lazy val vidDep = (inMessage \ "vidDep").extract[String]
    lazy val vidDepType = (inMessage \ "vidDepType").extract[String]
    lazy val vidDepName = (inMessage \ "vidDepName").extract[String]
    lazy val dataOperationAmountCurrency = (inMessage \ "data" \ "operationAmount" \ "currency").extract[String]
    lazy val dataStatus = (inMessage \ "data" \ "status").extract[String]
    lazy val dataTransactionDate = (inMessage \ "data" \ "transactionDate").extract[String]
    lazy val dataDebet = (inMessage \ "data" \ "debet").extract[Boolean]
    lazy val firstAdd = (inMessage \ "firstAdd").extract[Boolean]
    lazy val hash_empty_hash = HashUtils.getHashSHA256PrependingSalt("", propsModel.SHA256salt)
    lazy val feeAmountSum = BigDecimal((inMessage \ "data" \ "feeAmount" \ "sum").extract[String])
    lazy val feeAmountCurrency = (inMessage \ "data" \ "feeAmount" \ "currency").extract[String]

    lazy val transactionAmountSum = BigDecimal((inMessage \ "data" \ "transactionAmount" \ "sum").extract[String])
    lazy val transactionAmountCurrency = (inMessage \ "data" \ "transactionAmount" \ "currency").extract[String]

    lazy val transactionId = for {
      JObject(json) <- inMessage
      JField("transactions", JArray(transactions)) <- json
      JObject(transactions) <- transactions
      JField("transactionId", JString(transactionId)) <- transactions
    } yield transactionId

    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.CFT2RS_CD.eventTime")(0), eventTime)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.operationAmount.sum")(0), operationAmount) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.feeAmount.sum")(0), feeAmountSum) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.transactionAmount.sum")(0), transactionAmountSum)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.source_system")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.clientId")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.local_id")(0), internalId) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.KBO")(0), dataKBO) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.account")(0), dataAccount) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.systemId")(0), systemId) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDepType")(0), vidDepType) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDepName")(0), vidDepName) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.operationAmount.currency")(0), dataOperationAmountCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.data_status")(0), dataStatus) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDep")(0), vidDep) ++
      getMap[String](dtoMap("app.uaspdto.fields.data_transactionDate")(0), dataTransactionDate) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.empty.string.hash")(0), hash_empty_hash) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.transactionId")(0), transactionId.headOption.getOrElse("")) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.feeAmount.currency")(0), feeAmountCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.transactionAmount.currency")(0), transactionAmountCurrency)
    val dataBoolean = Map[String, Boolean]() ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.CFT2RS_CD.firstAdd")(0), firstAdd) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.data_debet")(0), dataDebet)

    UaspDto(
      id = mdmId,
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
