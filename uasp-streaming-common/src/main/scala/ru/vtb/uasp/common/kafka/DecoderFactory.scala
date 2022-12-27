package ru.vtb.uasp.common.kafka

import com.sksamuel.avro4s.{Decoder, ScalePrecision}
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.dto.UaspDto

object DecoderFactory {
  def getDecoder: (Decoder[UaspDto], GenericDatumReader[GenericRecord]) = {
    implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)
    val decoder = Decoder[UaspDto]
    val reader = new GenericDatumReader[GenericRecord](decoder.schema)
    (decoder, reader)
  }
}
