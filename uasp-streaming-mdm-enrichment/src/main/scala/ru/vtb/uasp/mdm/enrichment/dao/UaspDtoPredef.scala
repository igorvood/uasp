package ru.vtb.uasp.mdm.enrichment.dao

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.EnrichFields

import java.time.{LocalDateTime, ZoneId}

object UaspDtoPredef {

  implicit class PreDef(val self: UaspDto) extends AnyVal {
    /*вытаскиваю зн из соответствующей мапки*/
    def getValueFromMap(prop: EnrichFields): Option[Any] =
      getValueFromMapS(prop.fromFieldType, prop.fromFieldName)

    def getValueFromMapS(fromFieldType: String, fromFieldName: String): Option[Any] = {
      val option = fromFieldType.toUpperCase() match {
        case "STRING" => self.dataString.get(fromFieldName)
        case "BIGDECIMAL" => self.dataDecimal.get(fromFieldName)
        case "LONG" => self.dataLong.get(fromFieldName)
        case "INT" => self.dataInt.get(fromFieldName)
        case "FLOAT" => self.dataFloat.get(fromFieldName)
        case "DOUBLE" => self.dataDouble.get(fromFieldName)
        case "BOOLEAN" => self.dataBoolean.get(fromFieldName)
        case _ => throw new RuntimeException(s"Unrecognized type $fromFieldType, for message: ${self.toString}")
      }
      option
    }

    def enrichGlobalId(globalId: String, prop: EnrichFields): UaspDto = {
      val dString: Map[String, String] = self.dataString ++
        Map(prop.toFieldName -> globalId)
      self.copy(id = globalId,
        dataString = dString/*,
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli*/)
    }

  }


}
