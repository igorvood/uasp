package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object WithdrawUaspDtoDao {

  lazy val timeZone = "Europe/Moscow"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      sourceSumRub <- (inMessage \ "fields" \ "sourceSumRub").validate[BigDecimal]
      eventType <- (inMessage \ "eventType").validate[String]
      operationCode <- (inMessage \ "operationCode").validate[String]
      transferOrderId <- (inMessage \ "transferOrderId").validate[String]
      senderMdmId <- (inMessage \ "senderMdmId").validate[String]
      updatedAt <- (inMessage \ "updatedAt").validate[Double].map(q => q * 1000)
      targetMaskedPan <- (inMessage \ "fields" \ "targetMaskedPan").validateOpt[String]
      targetAccount <- (inMessage \ "fields" \ "targetAccount").validateOpt[String]
      targetBankRussianName <- (inMessage \ "fields" \ "targetBankRussianName").validateOpt[String]
      sourceMaskedPan <- (inMessage \ "fields" \ "sourceMaskedPan").validateOpt[String]
      sourceAccount <- (inMessage \ "fields" \ "sourceAccount").validate[String]
      receiverFpsBankId <- (inMessage \ "fields" \ "receiverFpsBankId").validateOpt[String]
      receiverName <- (inMessage \ "fields" \ "receiverName").validateOpt[String]
      senderName <- (inMessage \ "fields" \ "senderName").validateOpt[String]
      interactionChannel <- (inMessage \ "fields" \ "interactionChannel").validateOpt[String]
    } yield UaspDto(
      id = senderMdmId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.updatedAt")(0), updatedAt.toLong)),
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.sourceSumRub")(0), sourceSumRub)),
      dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.transferOrderId")(0), transferOrderId),
        getMapEntry[String](dtoMap("app.uaspdto.eventType")(0), eventType),
        getMapEntry[String](dtoMap("app.uaspdto.senderMdmId")(0), senderMdmId),
        getMapEntry[String](dtoMap("app.uaspdto.operationCode")(0), operationCode),
        getMapEntry[String](dtoMap("app.uaspdto.fields.targetMaskedPan")(0), targetMaskedPan.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.sourceAccount")(0), sourceAccount),
        getMapEntry[String](dtoMap("app.uaspdto.fields.targetAccount")(0), targetAccount.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.targetBankRussianName")(0), targetBankRussianName.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.sourceMaskedPan")(0), sourceMaskedPan.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.receiverFpsBankId")(0), receiverFpsBankId.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.receiverName")(0), receiverName.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.senderName")(0), senderName.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.interactionChannel")(0), interactionChannel.orNull)
      ),
      dataBoolean = Map.empty
    )
    List(value)

  }

}
