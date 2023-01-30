package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerIdProfileFullUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val contractId: String = (inMessage \ "contract_id").extract[String]
    lazy val customerId: String = (inMessage \ "customer_id").extract[String]
    lazy val contractNum: String = (inMessage \ "contract_num").extract[String]

    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]()
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]()
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.contract_id")(0), contractId) ++
      getMap[String](dtoMap("app.uaspdto.fields.customer_id")(0), customerId) ++
      getMap[String](dtoMap("app.uaspdto.fields.contract_num")(0), contractNum)
    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = contractId,
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
