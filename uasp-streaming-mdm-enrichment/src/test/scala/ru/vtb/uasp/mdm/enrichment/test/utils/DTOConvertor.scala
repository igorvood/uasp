package ru.vtb.uasp.mdm.enrichment.test.utils

import com.sksamuel.avro4s.{Decoder, ScalePrecision, SchemaFor}
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroDeserializeUtil

import java.nio.ByteBuffer

object DTOConvertor {

  def arrayByteToUaspDto(bytes: Array[Byte]): UaspDto = {
    implicit val sp: ScalePrecision = ScalePrecision(5, 23)

    val schema = SchemaFor[UaspDto]

    try {
      val decoder = Decoder[UaspDto]
      decoder.withSchema(schema)
      val reader = new GenericDatumReader[GenericRecord](decoder.schema)
      AvroDeserializeUtil.decode(ByteBuffer.wrap(bytes), decoder, reader)
    } catch {
      case e: Throwable => e match {
        case _ =>
          e.printStackTrace()
          throw new RuntimeException(e)
      }
    }
  }
}
