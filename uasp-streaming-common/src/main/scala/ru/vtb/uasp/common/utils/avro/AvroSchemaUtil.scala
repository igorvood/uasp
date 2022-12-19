package ru.vtb.uasp.common.utils.avro

import com.sksamuel.avro4s.{AvroSchema, ScalePrecision}
import ru.vtb.uasp.common.dto.UaspDto

object AvroSchemaUtil extends App {
  implicit val sp: ScalePrecision = ScalePrecision(5, 23)
  val schema = AvroSchema[UaspDto]
}
