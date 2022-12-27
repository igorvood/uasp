package ru.vtb.uasp.common.utils.avro

import com.sksamuel.avro4s.Encoder
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import org.apache.avro.io.EncoderFactory

import java.io.ByteArrayOutputStream

object AvroSerializeUtil {


  def encode[T](value: T, encoder: Encoder[T], writer: GenericDatumWriter[GenericRecord]): Array[Byte] = {

    val outputStream: ByteArrayOutputStream = null
    try {
      val outputStream = new ByteArrayOutputStream(512)
      val record = encoder.encode(value).asInstanceOf[GenericRecord]
      val enc = EncoderFactory.get().directBinaryEncoder(outputStream, null)
      writer.write(record, enc)
      val bytes = outputStream.toByteArray

      bytes
    } finally {
      if (outputStream != null) outputStream.close()
    }
  }

  //  def encode[T](value: T): Array[Byte] = {
  //
  //    implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)
  //    val schema = AvroSchema[T]
  //    val encoder = Encoder[T]
  //    val writer = new GenericDatumWriter[GenericRecord](schema)
  //
  //    val outputStream: ByteArrayOutputStream = null
  //    try {
  //      val outputStream = new ByteArrayOutputStream(512)
  //      val record = encoder.encode(value).asInstanceOf[GenericRecord]
  //      val enc = EncoderFactory.get().directBinaryEncoder(outputStream, null)
  //      writer.write(record, enc)
  //      val bytes = outputStream.toByteArray
  //
  //      bytes
  //    }finally {
  //      if (outputStream != null) outputStream.close()
  //    }
  //  }
}
