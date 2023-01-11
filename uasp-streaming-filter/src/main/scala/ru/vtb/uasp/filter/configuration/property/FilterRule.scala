package ru.vtb.uasp.filter.configuration.property

import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.filter.service.dto._

case class FilterRule(
                       fieldName: String,
                       operandClass: OperandClass,
                       operator: String
                     ) {
  require(fieldName.nonEmpty, "fieldName must be not empty")
  require(operator.nonEmpty, "operator must be not empty")
  require(operandClass != null, "operator must be not null")

  def operatorClass[T <: Comparable[T]]: FilterOperator[T] = operator match {
    case "!=" => NotEquals[T]()
    case "=" => Equals[T]()
    case ">" => Grater[T]()
    case ">=" => GraterOrEq[T]()
    case "<" => Less[T]()
    case "<=" => LessOrEq[T]()
    case "null" => Null[T]()
    case "notNull" => NotNull[T]()
    case x => throw new IllegalStateException(s"unsupported operator '$x' ")
  }


}

object FilterRule extends PropertyCombiner[FilterRule] {

  def getOperatorClass(clazz: String, value: Option[String]): OperandClass =
    clazz
    match {
      case "Int" => IntOperand(value.map(_.toInt))
      case "Long" => LongOperand(value.map(_.toLong))
      case "Float" => FloatOperand(value.map(_.toFloat))
      case "Double" => DoubleOperand(value.map(_.toDouble))
      case "BigDecimal" => BigDecimalOperand(value.map(BigDecimal(_)))
      case "String" => StringOperand(value)
      case "Boolean" => BooleanOperand(value.map(_.toBoolean))
      case unknown => throw new IllegalStateException(s"unsupported operandClass '$unknown' for prefix '$clazz'")
    }


  import ru.vtb.uasp.common.utils.config.PropertyUtil.{propertyVal, propertyValOptional, s}

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FilterRule] =
    for {
      fieldName <- propertyVal[String](prf, "fieldName")(appProps, configurationInitialise, s)
      operandClass <- propertyVal[String](prf, "operandClass")(appProps, configurationInitialise, s)
      compareWith <- propertyValOptional[String](prf, "compareWith")(appProps, configurationInitialise, s)
      operator <- propertyVal[String](prf, "operator")(appProps, configurationInitialise, s)
    } yield new FilterRule(
      fieldName = fieldName,
      operandClass = getOperatorClass(operandClass, compareWith),
      operator = operator
    )
}