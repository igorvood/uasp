package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CurrencyUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val currencyId: String = (inMessage \ "id").extract[String]
    lazy val currencyDate = dtStringToLong((inMessage \ "date").extract[String], "yyyy-MM-dd", "UTC")
    val result = for {
      JObject(json) <- inMessage
      JField("rates", JArray(rates)) <- json
      JObject(rates) <- rates
      JField("currency", JObject(currency)) <- rates
      JField("name", JString(name)) <- currency
      JField("numericCode", JString(numericCode)) <- currency
      JField("alphaCode", JString(alphaCode)) <- currency
      JField("price", JDouble(price)) <- rates
      JField("scale", JInt(scale)) <- rates
    } yield (price, scale, currency, name, numericCode, alphaCode)


    val dataInt = Map[String, Int]() ++
      getMap[Int](dtoMap("app.uaspdto.fields.rates_scale")(0), result.head._2.toInt)
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.currency_date")(0), currencyDate)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.rates_price")(0), result.head._1)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.id")(0), currencyId) ++
      getMap[String](dtoMap("app.uaspdto.fields.rates_currency_name")(0), result.head._4) ++
      getMap[String](dtoMap("app.uaspdto.fields.rates_currency_numericCode")(0), result.head._5) ++
      getMap[String](dtoMap("app.uaspdto.fields.rates_currency_alphaCode")(0), result.head._6)
    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = currencyId,
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
