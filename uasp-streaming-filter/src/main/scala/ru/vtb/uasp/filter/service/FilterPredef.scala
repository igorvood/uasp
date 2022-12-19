package ru.vtb.uasp.filter.service

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.filter.configuration.property.FilterRule
import ru.vtb.uasp.filter.service.dto._

import java.lang

object FilterPredef {

  implicit final class Predef(private val selfUasp: UaspDto) extends AnyVal {
    @inline def filterResult(filter: FilterRule): Boolean = {
      val triedBoolean = filter.operandClass match {
        case IntOperand(v) => filter.operatorClass.compare(selfUasp.dataInt.get(filter.fieldName).map(q => new Integer(q)), v)
        case LongOperand(v) => filter.operatorClass.compare(selfUasp.dataLong.get(filter.fieldName).map(q => new lang.Long(q)), v)
        case FloatOperand(v) => filter.operatorClass.compare(selfUasp.dataFloat.get(filter.fieldName).map(q => new lang.Float(q)), v)
        case DoubleOperand(v) => filter.operatorClass.compare(selfUasp.dataDouble.get(filter.fieldName).map(q => new lang.Double(q)), v)
        case BigDecimalOperand(v) => filter.operatorClass.compare(selfUasp.dataDecimal.get(filter.fieldName), v)
        case StringOperand(v) => filter.operatorClass.compare(selfUasp.dataString.get(filter.fieldName), v)
        case BooleanOperand(v) => filter.operatorClass.compare(selfUasp.dataBoolean.get(filter.fieldName).map(q => new lang.Boolean(q)), v)
      }
      triedBoolean
    }
  }

}
