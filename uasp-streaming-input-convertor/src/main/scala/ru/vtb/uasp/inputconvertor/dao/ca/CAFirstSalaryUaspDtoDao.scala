package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.CAFirstSalaryUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CAFirstSalaryUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[CAFirstSalaryUaspDto]
      .map(dj => {
        Json.toJson(
          UaspDto(
          id = dj.mdm_id_ca,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map.empty,
          dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.ca_first_salary.eventTime")(0), dj.event_dttm_ca_calc)),
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.ca_first_salary.data.operationAmount.sum")(0), dj.trans_amount_ca)),
          dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.clientId")(0), dj.mdm_id_ca),
            getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.data.KBO")(0), dj.kbo_ca),
            getMapEntry[String](dtoMap("app.uaspdto.fields.ca_first_salary.source_system")(0), dj.source_system_ca)
          ),
          dataBoolean = Map.empty
        )
        )
      })
    List(value1)


  }


}
