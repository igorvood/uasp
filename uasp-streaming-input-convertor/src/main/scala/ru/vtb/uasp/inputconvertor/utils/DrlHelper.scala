package ru.vtb.uasp.inputconvertor.utils

import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}

object DrlHelper {

  def checkFormatBigDecimal(value: Option[BigDecimal]): Boolean = value match {
    case None => true
    case Some(d) => d.scale > SCALE || d.precision > PRECISION
  }

}
