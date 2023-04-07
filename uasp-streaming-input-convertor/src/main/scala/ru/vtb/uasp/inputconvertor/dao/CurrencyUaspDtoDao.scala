package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.RatesDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CurrencyUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {


    val value = inMessage.validate[RatesDto]

    val value1 = value.map(rr => rr.rates.map(r =>
      Json.toJson(
        UaspDto(
          id = rr.id,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map(getMapEntry[Int](dtoMap("app.uaspdto.fields.rates_scale")(0), r.scale)),
          dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.currency_date")(0), rr.date)),
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.rates_price")(0), r.price)),
          dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.id")(0), rr.id),
            getMapEntry[String](dtoMap("app.uaspdto.fields.rates_currency_name")(0), r.currency.name),
            getMapEntry[String](dtoMap("app.uaspdto.fields.rates_currency_numericCode")(0), r.currency.numericCode),
            getMapEntry[String](dtoMap("app.uaspdto.fields.rates_currency_alphaCode")(0), r.currency.alphaCode)
          ),
          dataBoolean = Map.empty
        )
      )
    )
    )

    value1 match {
      case JsSuccess(value, path) => value.map(u => JsSuccess(u, path))
      case JsError(errors) => List(JsError(errors))
    }

  }

}
