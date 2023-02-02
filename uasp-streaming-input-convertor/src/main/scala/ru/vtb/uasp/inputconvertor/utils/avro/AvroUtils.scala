package ru.vtb.uasp.inputconvertor.utils.avro

import com.sksamuel.avro4s._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.utils.avro.AvroDeserializeUtil

import java.nio.ByteBuffer

object AvroUtils {

  implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)

  def avroDeserialize[T](in: Array[Byte])(implicit impDecoder: Decoder[T]): T = {
    val decoder = Decoder[T](impDecoder)
    val reader = new GenericDatumReader[GenericRecord](decoder.schema)
    AvroDeserializeUtil.decode[T](ByteBuffer.wrap(in), decoder, reader)
  }

  def avroDeserialize[T](in: Array[Byte], avroSchema: Schema)(implicit impDecoder: Decoder[T]): T = {
    val decoder = Decoder[T](impDecoder)
    val reader = new GenericDatumReader[GenericRecord](avroSchema)
    AvroDeserializeUtil.decode[T](ByteBuffer.wrap(in), decoder, reader)
  }

}
