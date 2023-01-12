package ru.vtb.bevent.first.salary.aggregate.Util

import ru.vtb.uasp.common.constants.BigDecimalConst

import java.math.MathContext
import scala.util.matching.Regex


object DrlCaseHelper {
  //TODO Fix to common property
  val mc = new MathContext(BigDecimalConst.PRECISION)

  def isCase8(withDraw: String, endBal: String): Boolean = {
    val endBalDouble = endBal.toDouble
    val withDrawDouble = withDraw.toDouble
    //TODO вынести в проперти
    val limit: Double = 10000.0

    if ((endBalDouble + withDrawDouble) >= limit

      && endBalDouble < limit)
      true
    else false
  }

  def isCase44(withDraw: String, amountAll: String): Boolean = {
    val amountAllDouble = amountAll.toDouble
    val withDrawDouble = withDraw.toDouble
    if (withDrawDouble / amountAllDouble >= 0.5) {
      true
    } else false
  }

  def isCase71(value: Option[Any]): Boolean = value match {
    case Some(s: String) => s.contains("Перенос начисленных процентов согласно условиям договора. Вклад")
    case _ => false
  }

  def getAccNumCase71(tranComment: String): String =
    getValueByPattern(tranComment, "\\d{20}".r)

  def getValueByPattern(in: String, pattern: Regex): String = pattern
    .findFirstMatchIn(in)
    .map(_.toString())
    .map(_.replaceAll("\"", ""))
    .getOrElse("0*******************0")

  //Fixme string to decimal
  def calculateCase44(withDraw: String, amountAll: String): Double = {
    val amountAllDouble = amountAll.toDouble
    val withDrawDouble = withDraw.toDouble
    withDrawDouble / amountAllDouble
  }

  //Fixme string to decimal
  def calculateRubSum(amount: String, price: String, scale: String): BigDecimal = {
    val amountTmp = setScaleToBigDecimal(BigDecimal(amount.toDouble, mc))
    val priceTmp = setScaleToBigDecimal(BigDecimal(price.toDouble, mc))
    val scaleTmp = if (!"0".equals(scale)) setScaleToBigDecimal(BigDecimal(scale.toDouble, mc)) else setScaleToBigDecimal(BigDecimal(1, mc))
    //TODO для 71 кейса округлить до 3х знаков после запятой
    setScaleToBigDecimal(amountTmp * priceTmp / scaleTmp)
  }

  def millisToHour(millis: Long): Long =
    millis / 1000 / 60 / 60.toLong

  def setScaleToBigDecimal(number: BigDecimal): BigDecimal =
    number.setScale(BigDecimalConst.SCALE, BigDecimal.RoundingMode.HALF_UP)
}
