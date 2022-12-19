package ru.vtb.uasp.mutator.configuration.drools

import com.typesafe.config.Optional
import ru.vtb.uasp.common.constants.BigDecimalConst

import scala.util.{Failure, Success, Try}

object DrlHelper {

  private val value: Int = Option[Int](1).get

  def checkFormatBigDecimal(value: Option[BigDecimal]): Boolean = value match {
    case None => true
    case Some(d) => d.scale > BigDecimalConst.SCALE || d.precision > BigDecimalConst.PRECISION
  }

  def isNonEmpty(value: Option[Any]): Boolean = value match {
    case Some(s: String) => s != ""
    case v => v.nonEmpty
  }

  def isEven(value: Option[Int]): Boolean = value.exists(n => n % 2 == 0)

  def toIntGt(first: Option[Any], second: Int): Boolean = first match {
    case Some(s: String) if s != "" => Try(s.toInt) match {
      case Success(i) => i > second
      case Failure(s) => false
    }
    case _ => false
  }

  def containsInList(ch: Any, v: java.util.List[java.lang.String]): Boolean = v.contains(ch)

  def inValue(ch: String, v: String*): Boolean = v.contains(ch)

  def notIn[T](ch: T, v: T*): Boolean = !in(ch, v)

  def in[T](ch: T, v: T*): Boolean = v.contains(ch)

  def isNone(obj: Any): Boolean = {
    obj == None
  }

}
