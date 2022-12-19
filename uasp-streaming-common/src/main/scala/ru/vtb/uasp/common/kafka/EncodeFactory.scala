package ru.vtb.uasp.common.kafka

import com.sksamuel.avro4s.{AvroSchema, Encoder, ScalePrecision}
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.dto.UaspDto


object EncodeFactory {
  def getEncode: (Encoder[UaspDto], GenericDatumWriter[GenericRecord]) = {
    implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)

    val schema = AvroSchema[UaspDto]
    val encoder = Encoder[UaspDto]
    val writer = new GenericDatumWriter[GenericRecord](schema)
    (encoder, writer)
  }

}
