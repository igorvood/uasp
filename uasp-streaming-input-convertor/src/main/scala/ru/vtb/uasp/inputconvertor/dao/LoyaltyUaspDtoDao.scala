package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object LoyaltyUaspDtoDao {
  def fromJValue(inMessage: JValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "loyalty"

    lazy val mdmId: String = (inMessage \ "mdmId").extract[String]
    lazy val eventDttm: String = (inMessage \ "actualTime").extract[String]
    lazy val eventDttmLong: Long = dtStringToLong(eventDttm, "yyyy-MM-dd'T'HH:mm:ss", "GMT+0000")
    lazy val hash_empty_hash = HashUtils.getHashSHA256PrependingSalt("", propsModel.sha256salt)
    lazy val eventType: String = (inMessage \ "eventType").extractOrElse[String]("")

    lazy val loyaltyPrograms = for {
      JObject(json) <- inMessage
      JField("loyaltyPrograms", JArray(loyaltyPrograms)) <- json
      JObject(loyaltyPrograms) <- loyaltyPrograms

      JField("name", JString(name)) <- loyaltyPrograms
      JField("code", JString(code)) <- loyaltyPrograms
      JField("status", JString(status)) <- loyaltyPrograms

      JField("options", JArray(options)) <- loyaltyPrograms
      JField("accounts", JArray(accounts)) <- loyaltyPrograms
    } yield (name, code, status, options, accounts)

    lazy val programs = loyaltyPrograms
      .map {
        case (name, code, status, rawOptions, rawAccounts) =>
          val options = for {
            JObject(options) <- rawOptions
            JField("code", JString(code)) <- options
            JField("name", JString(name)) <- options
            JField("startDate", startDate) <- options
            JField("closeDate", closeDate) <- options
            JField("createTime", createTime) <- options

          } yield (code, name, startDate match {
            case JString(startDate) => startDate
            case _ => None
          },
            closeDate match {
              case JString(closeDate) => closeDate
              case _ => None
            },
            createTime match {
              case JString(createTime) => createTime
              case _ => None
            })

          val accounts = for {
            JObject(options) <- rawAccounts
            JField("code", JString(currency)) <- options
            JField("balance", JString(name)) <- options
          } yield (currency, name)

          (name, code, status, options, accounts)
      }

    lazy val code = programs
      .flatMap { case (name, code, status, options, accounts) =>
        options
      }
      .map { case (code, name, startDate, closeDate, createTime) => code }
      .last


    val dataInt = Map[String, Int]()

    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.loyalty.eventDttm")(0), eventDttmLong)

    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()

    val dataDecimal = Map[String, BigDecimal]()

    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.loyalty.mdmId")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.loyalty.source_system")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.loyalty.loyaltyCode")(0), code) ++
      getMap[String](dtoMap("app.uaspdto.fields.loyalty.empty.string.hash")(0), hash_empty_hash) ++
      getMap[String](dtoMap("app.uaspdto.fields.loyalty.eventType")(0), eventType)


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

