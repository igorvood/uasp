package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import org.json4s.{DefaultFormats, Formats, JValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CAFirstSalaryUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val mdmId = (inMessage \ "customer_id").extract[String]
    lazy val transaction_dttm = (inMessage \ "transaction_dttm").extract[Long]
    lazy val dataKBO = (inMessage \ "kbo").extract[String]
    lazy val transaction_amt = BigDecimal((inMessage \ "transaction_amt").extract[String])
    lazy val source_system_cd = (inMessage \ "source_system_cd").extract[String]

    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.ca_first_salary.eventTime")(0), transaction_dttm)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.ca_first_salary.data.operationAmount.sum")(0), transaction_amt)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.ca_first_salary.clientId")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca_first_salary.data.KBO")(0), dataKBO) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca_first_salary.source_system")(0), source_system_cd)

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
