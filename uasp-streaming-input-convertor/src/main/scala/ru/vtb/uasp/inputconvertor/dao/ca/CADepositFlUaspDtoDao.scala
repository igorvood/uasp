package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import org.json4s.{DefaultFormats, Formats, JValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap


import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CADepositFlUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val mdmId = (inMessage \ "mdm_id").extract[String]
    lazy val account_num = (inMessage \ "account_num").extract[String]
    lazy val product_nm = (inMessage \ "product_nm").extract[String]
    lazy val product_rate = BigDecimal((inMessage \ "product_rate").extract[String])
    lazy val period = (inMessage \ "period").extract[Int]

    val dataInt = Map[String, Int]() ++
      getMap[Int](dtoMap("app.uaspdto.fields.depositfl.period")(0), period)
    val dataLong = Map[String, Long]()
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.depositfl.product_rate")(0), product_rate)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.depositfl.account_num")(0), account_num) ++
      getMap[String](dtoMap("app.uaspdto.fields.depositfl.mdmid")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.depositfl.product_nm")(0), product_nm)
    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = account_num,
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
