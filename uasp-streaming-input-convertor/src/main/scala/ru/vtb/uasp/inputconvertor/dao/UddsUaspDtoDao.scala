package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMapEntry}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object UddsUaspDtoDao {
  lazy val systemSource = "udds"

  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {


    val value = for {
      operationId <- (inMessage \ "OPERATION_ID").validate[String]
      mdmId <- (inMessage \ "MDM_ID").validate[String]
      eventDttm <- (inMessage \ "EVENT_DTTM").validate[String]
      eventDttmLong = dtStringToLong(eventDttm, "yyyy-MM-dd HH:mm:ss", "GMT+0000")
      operationAmount <- ((inMessage \ "OPERATION_AMOUNT").validate[BigDecimal])
      goalCurrency <- (inMessage \ "GOAL_CURRENCY").validate[String]
      tradingPlatform <- (inMessage \ "TRADING_PLATFORM").validate[String]
      balanceBeforeOperation <- (inMessage \ "BALANCE_BEFORE_OPERATION").validate[String]
      interactionChannel <- (inMessage \ "INTERACTION_CHANNEL").validate[String]
      operationType <- (inMessage \ "OPERATION_TYPE").validate[String]
      sourceAccount <- (inMessage \ "SOURCE_ACCOUNT").validate[String]
      sourceAccountBic <- (inMessage \ "SOURCE_ACCOUNT_BIC").validate[String]
      sourceAccountType <- (inMessage \ "SOURCE_ACCOUNT_TYPE").validate[String]
      targetAccount <- (inMessage \ "TARGET_ACCOUNT").validate[String]
      targetAccountBic <- (inMessage \ "TARGET_ACCOUNT_BIC").validate[String]
      targetAccountType <- (inMessage \ "TARGET_ACCOUNT_TYPE").validate[String]
      creditTransactionId <- (inMessage \ "CREDIT_TRANSACTION_ID").validate[String]
      hash_empty_hash = HashUtils.getHashSHA256PrependingSalt("", propsModel.sha256salt)
    } yield UaspDto(
      id = mdmId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), eventDttmLong)),
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.udds.operationAmount")(0), operationAmount)),
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.udds.mdmId")(0), mdmId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.operationId")(0), operationId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.goalCurrency")(0), goalCurrency),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.tradingPlatform")(0), tradingPlatform),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.balanceBeforeOperation")(0), balanceBeforeOperation),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.interactionChannel")(0), interactionChannel),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.operationType")(0), operationType),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccount")(0), sourceAccount),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccountBic")(0), sourceAccountBic),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccountType")(0), sourceAccountType),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccount")(0), targetAccount),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccountBic")(0), targetAccountBic),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccountType")(0), targetAccountType),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.creditTransactionId")(0), creditTransactionId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.system_source")(0), systemSource),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), eventDttm),
        getMapEntry[String](dtoMap("app.uaspdto.fields.udds.empty.string.hash")(0), hash_empty_hash)),
      dataBoolean = Map.empty
    )
    List(value)

  }

}
