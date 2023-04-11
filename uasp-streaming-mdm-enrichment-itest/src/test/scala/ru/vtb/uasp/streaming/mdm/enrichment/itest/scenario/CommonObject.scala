package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import com.sksamuel.avro4s.{AvroSchema, Decoder, Encoder}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import ru.vtb.uasp.common.dto.UaspDto

object CommonObject {
  val schemaUaspDto: Schema = AvroSchema[UaspDto]
  val decoderUaspDto = Decoder[UaspDto]
  val encoderUaspDto: Encoder[UaspDto] = Encoder[UaspDto]
  val genericDatumWriterUaspDto = new GenericDatumWriter[GenericRecord](schemaUaspDto)
  val genericDatumReaderUaspDto = new GenericDatumReader[GenericRecord](decoderUaspDto.schema)

}
