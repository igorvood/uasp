package ru.vtb.uasp.inputconvertor.utils.avro

import com.sksamuel.avro4s._
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.utils.avro.{AvroDeserializeUtil, AvroSerializeUtil}

import java.nio.ByteBuffer

object AvroUtils {

  implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)

  def avroSerialize[T](instanceT: T)(implicit schemaFor: SchemaFor[T], impEncoder: Encoder[T]): Array[Byte] = {
    val schema = AvroSchema[T](schemaFor)
    val encoder = Encoder[T](impEncoder)
    val writer = new GenericDatumWriter[GenericRecord](schema)
    AvroSerializeUtil.encode[T](instanceT, encoder, writer)
  }

  def avroSerialize[T](instanceT: T, avroSchema: Schema)(implicit impEncoder: Encoder[T]): Array[Byte] = {
    val encoder = Encoder[T](impEncoder)
    val writer = new GenericDatumWriter[GenericRecord](avroSchema)
    AvroSerializeUtil.encode[T](instanceT, encoder, writer)
  }

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
