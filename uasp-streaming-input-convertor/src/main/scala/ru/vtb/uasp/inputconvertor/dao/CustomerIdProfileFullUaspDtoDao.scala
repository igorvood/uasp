package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerIdProfileFullUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      contractId <- (inMessage \ "contract_id").validate[String]
      customerId <- (inMessage \ "customer_id").validate[String]
      contractNum <- (inMessage \ "contract_num").validateOpt[String]
    } yield UaspDto(
      id = contractId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map.empty,
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map.empty,
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.contract_id")(0), contractId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.customer_id")(0), customerId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.contract_num")(0), contractNum.orNull)
      ),
      dataBoolean = Map.empty
    )
    List(value)

  }
}
