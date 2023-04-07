package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.WithdrawUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object WithdrawUaspDtoDao {

  lazy val timeZone = "Europe/Moscow"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[WithdrawUaspDto].map(dj => {
      UaspDto(
        id = dj.senderMdmId,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.updatedAt")(0), dj.updatedAtMultiply.toLong)),
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.sourceSumRub")(0), dj.fields.sourceSumRub)),
        dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.transferOrderId")(0), dj.transferOrderId),
          getMapEntry[String](dtoMap("app.uaspdto.eventType")(0), dj.eventType),
          getMapEntry[String](dtoMap("app.uaspdto.senderMdmId")(0), dj.senderMdmId),
          getMapEntry[String](dtoMap("app.uaspdto.operationCode")(0), dj.operationCode),
          getMapEntry[String](dtoMap("app.uaspdto.fields.targetMaskedPan")(0), dj.fields.targetMaskedPan.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.sourceAccount")(0), dj.fields.sourceAccount),
          getMapEntry[String](dtoMap("app.uaspdto.fields.targetAccount")(0), dj.fields.targetAccount.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.targetBankRussianName")(0), dj.fields.targetBankRussianName.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.sourceMaskedPan")(0), dj.fields.sourceMaskedPan.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.receiverFpsBankId")(0), dj.fields.receiverFpsBankId.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.receiverName")(0), dj.fields.receiverName.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.senderName")(0), dj.fields.senderName.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.interactionChannel")(0), dj.fields.interactionChannel.orNull)
        ),
        dataBoolean = Map.empty
      )
    }).map(d => Json.toJson(d))

    List(value1)

  }

}
