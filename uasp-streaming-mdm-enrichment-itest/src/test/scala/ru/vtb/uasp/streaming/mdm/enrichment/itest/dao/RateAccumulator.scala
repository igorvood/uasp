package ru.vtb.uasp.streaming.mdm.enrichment.itest.dao

import play.api.libs.json.Json
import ru.vtb.uasp.mdm.enrichment.service.rate.dto.RateResponse
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.ConsumerRecordAccumulatorNew
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.prefix

import java.nio.charset.StandardCharsets

class RateAccumulator extends ConsumerRecordAccumulatorNew[Array[Byte], Array[Byte], String, RateResponse, Seq[RateResponse], String] {

  override val keyDecoder: Array[Byte] => String = { key => Option(key).map(s => new String(s, StandardCharsets.UTF_8)).getOrElse(throw new IllegalStateException("Почему-то ключ пустой")) }

  override val valueDecoder: Array[Byte] => RateResponse = { value =>
    Option(value)
      .map(s => new String(s, StandardCharsets.UTF_8))
      .map(strValue => Json.parse(strValue).validate[RateResponse].getOrElse(throw new IllegalStateException("не получилось десерилизовать")))
      .getOrElse(throw new IllegalStateException("Произошла ошибка"))
  }


  override val keyMapper: (String, RateResponse) => String = { (k, v) => v.originalMessageId }

  override val filterPredecate: (String, RateResponse) => Boolean = { (key, value) => value.originalMessageId.startsWith(prefix) }

  override def get(key: String): RateResponse = records.get(key)

  override def getAll(key: String): Seq[RateResponse] = Seq(records.get(key))
}
