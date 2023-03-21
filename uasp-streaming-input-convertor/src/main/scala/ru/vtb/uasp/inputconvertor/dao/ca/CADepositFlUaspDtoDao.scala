package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CADepositFlUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      mdmId <- (inMessage \ "mdm_id").validate[String]
      account_num <- (inMessage \ "account_num").validate[String]
      product_nm <- (inMessage \ "product_nm").validate[String]
      product_rate <- ((inMessage \ "product_rate").validate[BigDecimal])
      period <- (inMessage \ "period").validate[Int]
    } yield UaspDto(
      id = account_num,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map(getMapEntry[Int](dtoMap("app.uaspdto.fields.depositfl.period")(0), period)),
      dataLong = Map.empty,
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.depositfl.product_rate")(0), product_rate)),
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.account_num")(0), account_num),
        getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.mdmid")(0), mdmId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.product_nm")(0), product_nm)
      ),
      dataBoolean = Map.empty
    )
    List(value)


  }


}
