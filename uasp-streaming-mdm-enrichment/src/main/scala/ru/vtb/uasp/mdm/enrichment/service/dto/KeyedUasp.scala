package ru.vtb.uasp.mdm.enrichment.service.dto

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.service.dto.TypeAlias.UaspLocalId
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.EnrichFields

import scala.annotation.tailrec

case class KeyedUasp(localId: UaspLocalId,
                     uaspDto: UaspDto) {

  /*работает только для Горячего потока, надо пройтись по всем свойствам для обогащения, вытащить состояние по ним и записать его в dto*/
  def enrichMainStream(enrichPropL: List[EnrichFields])(implicit stateFun: String => Option[String]): Either[String, KeyedUasp] = {
    @tailrec
    def enrichOneByOne(uasp: Either[String, KeyedUasp], enrichPropertyL: List[EnrichFields]): Either[String, KeyedUasp] = {
      enrichPropertyL match {
        case Nil => uasp
        case x :: Nil => enr(uasp, x, stateFun)
        case x :: xs => enrichOneByOne(enr(uasp, x, stateFun), xs)
      }
    }

    enrichOneByOne(Right(this), enrichPropL)
  }

  private def enr(v: Either[String, KeyedUasp], x: EnrichFields, stateFun: String => Option[String]): Either[String, KeyedUasp] = {
    val value = stateFun(x.fromFieldName)
    for {
      ok <- v
      enrOk <- ok.enrichValue(value, x)
    } yield enrOk

  }

  private def enrichValue(value: Option[String], prop: EnrichFields): Either[String, KeyedUasp] = {
    val toFieldName = prop.toFieldName
    val uaspDto = this.uaspDto
    val product: Either[String, KeyedUasp] = (prop.isOptionalEnrichValue, value) match {
      case (_, Some(x)) =>
        val dto = prop.fromFieldType.toUpperCase() match {
          case "STRING" => uaspDto.copy(dataString = uaspDto.dataString + (toFieldName -> x))
          case "BIGDECIMAL" => uaspDto.copy(dataDecimal = uaspDto.dataDecimal + (toFieldName -> BigDecimal(x)))
          case "LONG" => uaspDto.copy(dataLong = uaspDto.dataLong + (toFieldName -> x.toLong))
          case "INT" => uaspDto.copy(dataInt = uaspDto.dataInt + (toFieldName -> x.toInt))
          case "FLOAT" => uaspDto.copy(dataFloat = uaspDto.dataFloat + (toFieldName -> x.toFloat))
          case "DOUBLE" => uaspDto.copy(dataDouble = uaspDto.dataDouble + (toFieldName -> x.toDouble))
          case "BOOLEAN" => uaspDto.copy(dataBoolean = uaspDto.dataBoolean + (toFieldName -> x.toBoolean))
          case _ => throw new RuntimeException(s"Unrecognized type $prop.fromFieldType, for message for message: ${uaspDto.toString}")
        }
        Right(this.copy(uaspDto = dto))
      case (false, None) => Left(s"Value from field ${prop.fromFieldName} to ${prop.toFieldName} is null, but value isn't optional")
      case (_, None) => Right(this)

    }
    product
  }

}
