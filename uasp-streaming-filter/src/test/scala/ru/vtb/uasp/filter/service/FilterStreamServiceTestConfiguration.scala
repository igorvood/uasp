package ru.vtb.uasp.filter.service

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.AllApplicationProperties
import ru.vtb.uasp.filter.configuration.property.{FilterConfiguration, FilterRule}
import ru.vtb.uasp.filter.service.dto._

import java.util.Calendar

object FilterStreamServiceTestConfiguration {

  val uaspDtoWithFilelds: Array[UaspDto] = (1 to 9)
    .map(q =>
      UaspDto(id = "id",
        dataInt = Map("someInt" -> q),
        dataLong = Map("someLong" -> q),
        dataFloat = Map("someFloat" -> q),
        dataDouble = Map("someDouble" -> q),
        dataDecimal = Map("someBigDecimal" -> BigDecimal(q)),
        dataString = Map("someString" -> q.toString),
        dataBoolean = Map("someBoolean" -> {
          val bool = if (q % 2 == 1) true else false
          bool
        }
        ),
        uuid = "String",
        process_timestamp = Calendar.getInstance.getTimeInMillis)).toArray
  val dtoNoFields: UaspDto = UaspDto(id = "id",
    dataInt = Map(),
    dataLong = Map(),
    dataFloat = Map(),
    dataDouble = Map(),
    dataDecimal = Map(),
    dataString = Map(),
    dataBoolean = Map(),
    uuid = "String",
    process_timestamp = Calendar.getInstance.getTimeInMillis)
  private val propPrefix = "app.filter."

  def expectedMap: Map[String, FilteredCount] = filterRulesMap
    .map(fr => {
      val expectCount = (fr._2.operatorClass, fr._2.operandClass) match {
        case (NotEquals(), BooleanOperand(_)) => FilteredCount(4, 5)
        case (Equals(), BooleanOperand(_)) => FilteredCount(5, 4)
        case (Grater(), BooleanOperand(_)) => FilteredCount(0, 9)
        case (GraterOrEq(), BooleanOperand(_)) => FilteredCount(5, 4)
        case (Less(), BooleanOperand(_)) => FilteredCount(4, 5)
        case (LessOrEq(), BooleanOperand(_)) => FilteredCount(9, 0)

        case (NotEquals(), _) => FilteredCount(8, 1)
        case (Equals(), _) => FilteredCount(1, 8)
        case (Grater(), _) => FilteredCount(5, 4)
        case (GraterOrEq(), _) => FilteredCount(6, 3)
        case (Less(), _) => FilteredCount(3, 6)
        case (LessOrEq(), _) => FilteredCount(4, 5)
        case (Null(), _) => FilteredCount(0, 9)
        case (NotNull(), _) => FilteredCount(9, 0)
      }
      fr._1 -> expectCount
    }
    )

  def filterRulesMap: Map[String, FilterRule] = operator
    .flatMap(oper => {
      operandClass
        .map(operClass => {
          val value: String = if (operClass != "Boolean") "4" else "true"
          val propMap: Map[String, String] = Map(s"$propPrefix$operClass${oper._1}.operandClass" -> operClass,
            s"$propPrefix$operClass${oper._1}.operator" -> oper._2,
            s"$propPrefix$operClass${oper._1}.fieldName" -> s"some$operClass",
            s"$propPrefix$operClass${oper._1}.compareWith" -> value,
            s"$propPrefix$operClass${oper._1}.tagPrefix" -> s"$operClass${oper._1}"
          )
          s"$operClass${oper._1}" -> propMap
        })
        .map(propMap => propMap._1 -> AllApplicationProperties(propMap._2))
        .map(applicationPropertiesMap => applicationPropertiesMap._1 -> FilterRule(propPrefix + applicationPropertiesMap._1)(applicationPropertiesMap._2, FilterConfiguration))
    }
    )

  private def operandClass = Set("Int", "Long", "Float", "Double", "BigDecimal", "String", "Boolean")

  private def operator = Map(
    "NotEquals" -> "!=",
    "Equals" -> "=",
    "Gather" -> ">",
    "GatherOrEquals" -> ">=",
    "Less" -> "<",
    "LessOrEquals" -> "<=",
    "Null" -> "null",
    "NotNull" -> "notNull",
  )

}
