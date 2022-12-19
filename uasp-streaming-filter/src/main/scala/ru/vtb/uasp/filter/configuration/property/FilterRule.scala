package ru.vtb.uasp.filter.configuration.property

import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.filter.service.dto._

case class FilterRule(
                       tagPrefix: String,
                       fieldName: String,
                       //                       compareWith: String,
                       operandClass: OperandClass,
                       operator: String
                     ) {
  require(tagPrefix.nonEmpty, "tagPrefix must be not empty")
  require(fieldName.nonEmpty, "fieldName must be not empty")
  //  require(compareWith.nonEmpty, "compareWith must be not empty")
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


  override def toString: String =
    s"""FilterRule{
       |  fieldName = $fieldName
       |  tagPrefix = $tagPrefix
       |  operatorClass = ${operatorClass.getClass}
       |  operandClass = ${operandClass.getClass}
       |}""".stripMargin

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


  import ru.vtb.uasp.common.utils.config.PropertyUtil._

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FilterRule] =
    for {
      tagPrefix <- propertyVal[String](prf, "tagPrefix")(appProps, configurationInitialise, s)
      fieldName <- propertyVal[String](prf, "fieldName")(appProps, configurationInitialise, s)
      operandClass <- propertyVal[String](prf, "operandClass")(appProps, configurationInitialise, s)
      compareWith <- propertyValOptional[String](prf, "compareWith")(appProps, configurationInitialise, s)
      operator <- propertyVal(prf, "operator")(appProps, configurationInitialise, s)
    } yield new FilterRule(
      tagPrefix = tagPrefix,
      fieldName = fieldName,
      operandClass = getOperatorClass(operandClass, compareWith),
      operator = operator

    )
}