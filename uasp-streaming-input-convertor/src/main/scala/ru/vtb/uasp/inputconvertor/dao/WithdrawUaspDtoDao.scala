package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId}
import java.util.{TimeZone, UUID}

object WithdrawUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    ///таймзона в которой приходит дата
    lazy val timeZone = "Europe/Moscow"


    lazy val sourceSumRub = BigDecimal((inMessage \ "fields" \ "sourceSumRub").extract[String])
    lazy val eventType = (inMessage \ "eventType").extract[String]
    lazy val operationCode = (inMessage \ "operationCode").extract[String]
    lazy val transferOrderId = (inMessage \ "transferOrderId").extract[String]
    lazy val senderMdmId = (inMessage \ "senderMdmId").extract[String]
    lazy val updatedAt = ((inMessage \ "updatedAt").extract[Double] * 1000 ).toLong
    lazy val targetMaskedPan = (inMessage \ "fields" \ "targetMaskedPan").extract[String]
    lazy val targetAccount = (inMessage \ "fields" \ "targetAccount").extract[String]
    lazy val targetBankRussianName = (inMessage \ "fields" \ "targetBankRussianName").extract[String]
    lazy val sourceMaskedPan = (inMessage \ "fields" \ "sourceMaskedPan").extract[String]
    lazy val sourceAccount = (inMessage \ "fields" \ "sourceAccount").extract[String]
    lazy val receiverFpsBankId = (inMessage \ "fields" \ "receiverFpsBankId").extract[String]
    lazy val receiverName = (inMessage \ "fields" \ "receiverName").extract[String]
    lazy val senderName = (inMessage \ "fields" \ "senderName").extract[String]
    lazy val interactionChannel = (inMessage \ "fields" \ "interactionChannel").extract[String]


    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]()  ++
      getMap[Long](dtoMap("app.uaspdto.updatedAt")(0), updatedAt.toLong)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.sourceSumRub")(0), sourceSumRub)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.transferOrderId")(0), transferOrderId) ++
      getMap[String](dtoMap("app.uaspdto.eventType")(0), eventType) ++
      getMap[String](dtoMap("app.uaspdto.senderMdmId")(0), senderMdmId) ++
      getMap[String](dtoMap("app.uaspdto.operationCode")(0), operationCode) ++
      getMap[String](dtoMap("app.uaspdto.fields.targetMaskedPan")(0), targetMaskedPan) ++
      getMap[String](dtoMap("app.uaspdto.fields.sourceAccount")(0), sourceAccount) ++
      getMap[String](dtoMap("app.uaspdto.fields.targetAccount")(0), targetAccount) ++
      getMap[String](dtoMap("app.uaspdto.fields.targetBankRussianName")(0), targetBankRussianName) ++
      getMap[String](dtoMap("app.uaspdto.fields.sourceMaskedPan")(0), sourceMaskedPan) ++
      getMap[String](dtoMap("app.uaspdto.fields.receiverFpsBankId")(0), receiverFpsBankId) ++
      getMap[String](dtoMap("app.uaspdto.fields.receiverName")(0), receiverName) ++
      getMap[String](dtoMap("app.uaspdto.fields.senderName")(0), senderName) ++
      getMap[String](dtoMap("app.uaspdto.fields.interactionChannel")(0), interactionChannel)

    val dataBoolean = Map[String, Boolean]()

   val result = UaspDto(
      id = senderMdmId,
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
    result
  }

}
