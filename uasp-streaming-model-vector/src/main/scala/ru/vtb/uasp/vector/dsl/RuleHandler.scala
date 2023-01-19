package ru.vtb.uasp.vector.dsl

import com.sksamuel.avro4s.ScalePrecision
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsString, JsValue, Json}
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.dto.MapType.MapType
import ru.vtb.uasp.vector.dsl.dto.{CaseRules, MapType, RuleField, TransformedData}
import ru.vtb.uasp.vector.util.UaspUtil

import scala.util.{Failure, Success, Try}

object RuleHandler {
  implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)
  private val separator = ","
  private val systemClassification = "system-classification"

  def applyAllRulesForUasp(uaspDto: UaspDto): Seq[(CaseRules, Either[Throwable, JsObject])] = {
    val cases = uaspDto
      .dataString(systemClassification)
      .split(separator).toSeq
      .map(a => {
        Try {
          RuleParser.caseRulesMap(a)
        } match {
          case Failure(exception) => Left(a)
          case Success(value) => Right(value)
        }
      })
      .map(c => {
        Try {
          if (c.isRight) {
            val caseRule = c.right.get
            (caseRule, Right(applyCase(uaspDto, caseRule)))
          } else {
            throw new RuntimeException(s"Правило для кейса: ${c.left.get} отсутсвует")
          }
        } match {
          case Failure(exception) => (null, Left(exception))
          case Success(value) => value
        }
      })
      .filter(js => (js._2.isRight && js._2.right.get.fields.nonEmpty) || js._2.isLeft)

    cases
  }

  def applyCase(uaspDto: UaspDto, rules: CaseRules): JsObject = {
    val transformedData = rules
      .fields
      .flatMap(field => field.mapping.get(UaspUtil.getSystemName(uaspDto).get.toLowerCase))
      .map(rf => transformRuleField(uaspDto, rf))

    val js = transformedData
      .map(f => valueToJsonValue(f.ruleField, f.value))

    JsObject(js)
  }

  def valueToJsonValue(ruleField: RuleField, value: Any): (String, JsValue) = {
    val convertedValue = ruleField.destinationType match {
      case MapType.INTEGER => JsNumber(BigDecimal(value.toString))
      case MapType.LONG => JsNumber(BigDecimal(value.toString))
      case MapType.FLOAT => JsNumber(BigDecimal(value.toString))
      case MapType.DOUBLE => JsNumber(BigDecimal(value.toString))
      case MapType.DECIMAL => JsNumber(BigDecimal(value.toString))
      case MapType.STRING => JsString(value.toString)
      case MapType.BOOLEAN => JsBoolean(value.toString.toBoolean)
    }

    (ruleField.destinationName, convertedValue)
  }

  def transformRuleField(uasp: UaspDto, rf: RuleField): TransformedData = {
    val sourceName = rf.sourceName
    val sourceType = rf.sourceType

    val value = getMapByTypeAndName(uasp, sourceName, sourceType, rf.requirement)
    val targetValuer = castValueToTargetType(value.toString, rf.destinationType)
    val transformFunctionValue = CustomFunctionHandler.customFunctions(rf.transformFunction)(targetValuer, uasp)

    TransformedData(transformFunctionValue, rf)
  }

  def getMapByTypeAndName(uaspDto: UaspDto, sourceName: String, sourceType: MapType, requirement: Boolean): Any = {
    Try {
      sourceType match {
        case MapType.ID => uaspDto.id
        case MapType.INTEGER => uaspDto.dataInt.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип INTEGER") else 0)
        case MapType.LONG => uaspDto.dataLong.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип LONG") else 0L)
        case MapType.FLOAT => uaspDto.dataFloat.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип FLOAT") else 0.0)
        case MapType.DOUBLE => uaspDto.dataDouble.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип DOUBLE") else 0.0)
        case MapType.DECIMAL => uaspDto.dataDecimal.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип DECIMAL") else BigDecimal(0))
        case MapType.STRING => uaspDto.dataString.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип STRING") else "")
        case MapType.BOOLEAN => uaspDto.dataBoolean.getOrElse(sourceName, if (requirement) throw new RuntimeException(s"Поле отсутвует: $sourceName. Тип BOOLEAN") else false)
        case MapType.EMPTY => ""
      }
    } match {
      case Failure(exception) => throw new RuntimeException(exception)
      case Success(value) => value.toString
    }
  }

  def castValueToTargetType(value: String, targetType: MapType): Any = {
    val field = targetType match {
      case MapType.INTEGER => value.toInt
      case MapType.LONG => value.toLong
      case MapType.FLOAT => value.toFloat
      case MapType.DOUBLE => value.toDouble
      case MapType.DECIMAL => BigDecimal(value)
      case MapType.STRING => value
      case MapType.BOOLEAN => value.toBoolean
    }

    field
  }
}
