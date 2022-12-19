package ru.vtb.uasp.common.dto

import com.sksamuel.avro4s.{AvroSchema, Decoder, Encoder, ScalePrecision}
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.common.constants.BigDecimalConst
import ru.vtb.uasp.common.dto.UaspDto.zoneId

import java.time.{LocalDateTime, ZoneId}

case class UaspDto(id: String,
                   dataInt: Map[String, Int],
                   dataLong: Map[String, Long],
                   dataFloat: Map[String, Float],
                   dataDouble: Map[String, Double],
                   dataDecimal: Map[String, BigDecimal],
                   dataString: Map[String, String],
                   dataBoolean: Map[String, Boolean],
                   uuid: String,
                   process_timestamp: Long) extends Identity {

  def copy(id: String = UaspDto.this.id,
           dataInt: Map[String, Int] = UaspDto.this.dataInt,
           dataLong: Map[String, Long] = UaspDto.this.dataLong,
           dataFloat: Map[String, Float] = UaspDto.this.dataFloat,
           dataDouble: Map[String, Double] = UaspDto.this.dataDouble,
           dataDecimal: Map[String, BigDecimal] = UaspDto.this.dataDecimal,
           dataString: Map[String, String] = UaspDto.this.dataString,
           dataBoolean: Map[String, Boolean] = UaspDto.this.dataBoolean,
           process_timestamp: Long = LocalDateTime.now().atZone(zoneId).toInstant.toEpochMilli
          ): UaspDto = {
    new UaspDto(id = id,
      dataInt = dataInt,
      dataLong = dataLong,
      dataFloat = dataFloat,
      dataDouble = dataDouble,
      dataDecimal = dataDecimal,
      dataString = dataString,
      dataBoolean = dataBoolean,
      uuid = this.uuid,
      process_timestamp = process_timestamp
    )

  }

}


object UaspDto {

  private val zoneId = ZoneId.systemDefault()

  implicit val sp: ScalePrecision = BigDecimalConst.sp

  val decoderUasp = Decoder[UaspDto]

  val encoderUasp = Encoder[UaspDto]

  val schemaUasp = AvroSchema[UaspDto]

  val genericDatumReader = new GenericDatumReader[GenericRecord](schemaUasp)

  implicit val uaspJsonReads: Reads[UaspDto] = Json.reads[UaspDto]
  implicit val uaspJsonWrites: OWrites[UaspDto] = Json.writes[UaspDto]
}