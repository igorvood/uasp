package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.dao.UaspDtoPredef.PreDef
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json.NodeJsonMeta
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.{EnrichFields, KeySelectorProp}

trait EnrichPropertyFields {

  val keySelectorMain: KeySelectorProp

  val keySelectorEnrich: KeySelectorProp

  val fields: List[EnrichFields]

  lazy val flatProperty: NodeJsonMeta = NodeJsonMeta(fields.map(f => f.fromFieldName -> f.fromFieldType.toUpperCase()).toMap)

  private val keySelectorEnrichJsonMeta = for {key <- keySelectorEnrich.mapKey
                                               t <- keySelectorEnrich.mapType
                                               } yield NodeJsonMeta(Map(key -> t))

  require(fields.toSet.size == fields.size, "fields must contains unique record")

  def calcKey(in: UaspDto): Either[String, KeyedUasp] = {
    if (keySelectorMain.isId) {
      Option(in.id)
        .map(a => Right[String, KeyedUasp](KeyedUasp(a, in)))
        .getOrElse {
          Left[String, KeyedUasp](s"Uasp dto 'id' is null")
        }

    } else {
      val value = in.getValueFromMapS(keySelectorMain.mapType.get, keySelectorMain.mapKey.get)
        .map(key => Right[String, KeyedUasp](KeyedUasp(key.toString, in)))
        .getOrElse(
          Left[String, KeyedUasp](s"Unable to find value in map ${keySelectorMain.mapType.get} for group by key ${keySelectorMain.mapKey.get}")
        )
      value
    }
  }

  def validateFieldsAndExtractData(value: JsValue): Either[String, KeyedCAData] = {

    val mapJsValue = flatProperty.extract(value)
      .map(m => m.filter(q => q._2 != null))
    val maybeStateData = mapJsValue
      .flatMap(d => {
        val stateKeyValList = fields
          .map { f =>
            val valueForState = d.get(f.fromFieldName)

            val mayBeValidState = (f.isOptionalEnrichValue, valueForState) match {
              /*поле обязательное, но зн нет, значит надо добавить ошибку в массив*/
              case (false, None) => Left(s"Value for key ${f.fromFieldName} is null, but value isn't optional")
              /*поле не обязательное, и зн нет, надо просто очистить возможное состояние и ничего не делать*/
              case (true, None) => Right(None)
              /*поле не важно какое, и зн есть, надо просто сохранить зн в состояние*/
              case (_, Some(x)) => Right(Some(f.fromFieldName -> x))
            }
            mayBeValidState

          }
        val errs = stateKeyValList.
          collect { case q: Left[String, Option[(String, String)]] => q.value }

        val stringOrStringToString = if (errs.isEmpty) {
          val newStateVal = stateKeyValList
            .collect { case q: Right[String, Option[(String, String)]] => q.value }
            .collect { case q: Some[(String, String)] => q.value }
            .toMap

          val keyedValue = for {
            field <- keySelectorEnrich.mapKey
            meta <- keySelectorEnrichJsonMeta
            m <- meta.extract(value).toOption
            key <- m.get(field)
          } yield KeyedCAData(key, None, newStateVal)
          keyedValue
            .map(d => Right(d))
            .getOrElse(Left(s"Unable to find key value ${keySelectorEnrich.mapKey}"))
        } else {
          Left(errs.mkString(","))
        }
        stringOrStringToString
      }


      )

    maybeStateData
  }

  def validateFieldsAndExtractData(uaspDto: UaspDto): Either[String, KeyedCAData] = {

    val keyValueData: List[Either[String, Option[(String, String)]]] = fields.map { f =>
      val valueForState = uaspDto.getValueFromMap(f)

      (f.isOptionalEnrichValue, valueForState) match {
        /*поле обязательное, но зн нет, значит надо добавить ошибку в массив*/
        case (false, None) => Left(s"Value for key ${f.fromFieldName} is null, but value isn't optional")
        /*поле не обязательное, и зн нет, надо просто очистить возможное состояние и ничего не делать*/
        case (true, None) => Right(None)
        /*поле не важно какое, и зн есть, надо просто сохранить зн в состояние*/
        case (_, Some(x)) => Right(Some(f.fromFieldName -> x.toString))
      }
    }
    val validData = keyValueData
      .collect { case q: Right[String, Option[(String, String)]] => q.value }
    if (validData.size == keyValueData.size) {
      val newStateVal = validData
        .collect { case q: Some[(String, String)] => q.value }
        .toMap

      val key = if (keySelectorEnrich.isId) {
        Option(uaspDto.id)
          .map(d => Right(d))
          .getOrElse(Left(s"Field 'id' in UaspDto is null, unable to use it on key"))

      }
      else {
        uaspDto.getValueFromMapS(keySelectorEnrich.mapType.get, keySelectorEnrich.mapKey.get)
          .map(key => Right(key.toString))
          .getOrElse(Left(s"Unable to read key from field ${keySelectorEnrich.mapKey.get} with type ${keySelectorEnrich.mapType.get}"))
      }

      for {k <- key
           } yield KeyedCAData(k, None, newStateVal)

    } else {
      val errs = keyValueData.
        collect { case q: Left[String, Option[(String, String)]] => q.value }
      Left(errs.mkString(","))
    }

  }


}


object EnrichPropertyFields {

  val undefined = "undefined"


}