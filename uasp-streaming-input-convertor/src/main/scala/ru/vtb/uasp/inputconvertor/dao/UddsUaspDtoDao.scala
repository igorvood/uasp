package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.UddsUaspDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object UddsUaspDtoDao {
  lazy val systemSource = "udds"

  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[UddsUaspDto].map(dj => {
      UaspDto(
        id = dj.MDM_ID,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), dj.eventDttmLong)),
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.udds.operationAmount")(0), dj.OPERATION_AMOUNT)),
        dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.udds.mdmId")(0), dj.MDM_ID),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.operationId")(0), dj.OPERATION_ID),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.goalCurrency")(0), dj.GOAL_CURRENCY),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.tradingPlatform")(0), dj.TRADING_PLATFORM),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.balanceBeforeOperation")(0), dj.BALANCE_BEFORE_OPERATION),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.interactionChannel")(0), dj.INTERACTION_CHANNEL),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.operationType")(0), dj.OPERATION_TYPE),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccount")(0), dj.SOURCE_ACCOUNT),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccountBic")(0), dj.SOURCE_ACCOUNT_BIC),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.sourceAccountType")(0), dj.SOURCE_ACCOUNT_TYPE),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccount")(0), dj.TARGET_ACCOUNT),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccountBic")(0), dj.TARGET_ACCOUNT_BIC),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.targetAccountType")(0), dj.TARGET_ACCOUNT_TYPE),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.creditTransactionId")(0), dj.CREDIT_TRANSACTION_ID),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.system_source")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), dj.EVENT_DTTM),
          getMapEntry[String](dtoMap("app.uaspdto.fields.udds.empty.string.hash")(0), HashUtils.getHashSHA256PrependingSalt("", propsModel.sha256salt))),
        dataBoolean = Map.empty
      )
    }).map(d => Json.toJson(d))

    List(value1)

  }

}
