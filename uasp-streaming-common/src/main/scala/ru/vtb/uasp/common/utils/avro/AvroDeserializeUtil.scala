package ru.vtb.uasp.common.utils.avro

import com.sksamuel.avro4s.Decoder
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.DecoderFactory
import org.apache.avro.util.ByteBufferInputStream

import java.nio.ByteBuffer
import java.util.Collections

object AvroDeserializeUtil {


  def decode[T](bytes: ByteBuffer, decoder: Decoder[T], reader: GenericDatumReader[GenericRecord]): T = {
    var byteBuffer: ByteBufferInputStream = null
    try {
      byteBuffer = new ByteBufferInputStream(Collections.singletonList(bytes.duplicate))
      val dec = DecoderFactory.get().binaryDecoder(byteBuffer, null)
      val record = reader.read(null, dec)
      decoder.decode(record)
    }
    finally {
      if (byteBuffer != null) byteBuffer.close()
    }
  }
}
