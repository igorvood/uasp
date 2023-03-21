package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CAFirstSalaryUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      mdmId <- (inMessage \ "mdm_id_ca").validate[String]
      transaction_dttm <- (inMessage \ "event_dttm_ca").validate[Long].map(q => q * 1000)
      dataKBO <- (inMessage \ "kbo_ca").validate[String]
      transaction_amt <- (inMessage \ "trans_amount_ca").validate[BigDecimal]
      source_system_cd <- (inMessage \ "source_system_ca").validate[String]

    } yield UaspDto(
      id = mdmId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.ca_first_salary.eventTime")(0), transaction_dttm)),
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.ca_first_salary.data.operationAmount.sum")(0), transaction_amt)),
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.clientId")(0), mdmId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.data.KBO")(0), dataKBO),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.source_system")(0), source_system_cd)
      )
      ,
      dataBoolean = Map.empty
    )
    List(value)


  }


}
