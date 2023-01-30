package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object UddsUaspDtoDao {
  def fromJValue(inMessage: JValue, propsModel: NewInputPropsModel, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "udds"

    lazy val operationId: String = (inMessage \ "OPERATION_ID").extract[String]
    lazy val mdmId: String = (inMessage \ "MDM_ID").extract[String]
    lazy val eventDttm: String = (inMessage \ "EVENT_DTTM").extract[String]
    lazy val eventDttmLong: Long = dtStringToLong(eventDttm, "yyyy-MM-dd HH:mm:ss", "GMT+0000")
    lazy val operationAmount: BigDecimal = BigDecimal((inMessage \ "OPERATION_AMOUNT").extract[String])
    lazy val goalCurrency: String = (inMessage \ "GOAL_CURRENCY").extract[String]
    lazy val tradingPlatform: String = (inMessage \ "TRADING_PLATFORM").extract[String]
    lazy val balanceBeforeOperation: String = (inMessage \ "BALANCE_BEFORE_OPERATION").extract[String]
    lazy val interactionChannel: String = (inMessage \ "INTERACTION_CHANNEL").extract[String]
    lazy val operationType: String = (inMessage \ "OPERATION_TYPE").extract[String]
    lazy val sourceAccount: String = (inMessage \ "SOURCE_ACCOUNT").extract[String]
    lazy val sourceAccountBic: String = (inMessage \ "SOURCE_ACCOUNT_BIC").extract[String]
    lazy val sourceAccountType: String = (inMessage \ "SOURCE_ACCOUNT_TYPE").extract[String]
    lazy val targetAccount: String = (inMessage \ "TARGET_ACCOUNT").extract[String]
    lazy val targetAccountBic: String = (inMessage \ "TARGET_ACCOUNT_BIC").extract[String]
    lazy val targetAccountType: String = (inMessage \ "TARGET_ACCOUNT_TYPE").extract[String]
    lazy val creditTransactionId: String = (inMessage \ "CREDIT_TRANSACTION_ID").extract[String]
    lazy val hash_empty_hash = HashUtils.getHashSHA256PrependingSalt("", propsModel.SHA256salt)

    val dataInt = Map[String, Int]()

    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), eventDttmLong)

    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()

    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.udds.operationAmount")(0), operationAmount)

    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.mdmId")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.operationId")(0), operationId) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.goalCurrency")(0), goalCurrency) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.tradingPlatform")(0), tradingPlatform) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.balanceBeforeOperation")(0), balanceBeforeOperation) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.interactionChannel")(0), interactionChannel) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.operationType")(0), operationType) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.sourceAccount")(0), sourceAccount) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.sourceAccountBic")(0), sourceAccountBic) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.sourceAccountType")(0), sourceAccountType) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.targetAccount")(0), targetAccount) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.targetAccountBic")(0), targetAccountBic) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.targetAccountType")(0), targetAccountType) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.creditTransactionId")(0), creditTransactionId) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.system_source")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.eventDttm")(0), eventDttm) ++
      getMap[String](dtoMap("app.uaspdto.fields.udds.empty.string.hash")(0), hash_empty_hash)


    val dataBoolean = Map[String, Boolean]()

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
