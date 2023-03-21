package ru.vtb.uasp.inputconvertor.dao

import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.TimeZone
import scala.annotation.tailrec

object CommonDao {
  private val logger = LoggerFactory.getLogger(getClass)

  def getMap[T](mapKey: String, mapValue: => T): Map[String, T] = {
    try {
      val res: T = mapValue
      logger.info("mapKey: " + mapKey + ", res: " + res + ", " + Map(mapKey -> res))
      Map(mapKey -> res)
    } catch {
      case e: Throwable =>
        logger.warn("key: " + mapKey + ", " + e.getMessage)
        Map[String, T]()
    }
  }


  def getMapO[T](mapKey: String, mapValue: => Option[T]): Map[String, T] = {
    mapValue.map(res => Map(mapKey -> res)).getOrElse(Map.empty)
  }

  def getMapEntry[T](mapKey: String, mapValue: => T): (String, T) = {
    mapKey -> mapValue
  }

  def mapCollect[T](elems: (String, T)*): Map[String, T] = elems.filter(q => q._2 != null).toMap

  def dtStringToLong(inStr: String, fmt: String): Long = new SimpleDateFormat(fmt).parse(inStr).getTime

  def dtStringToLong(inStr: String, fmt: String, timeZone: String): Long = {
    val sdf = new SimpleDateFormat(fmt)
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone))
    sdf.parse(inStr).getTime
  }

  def findAndMaskNumber(input: String): String = {
    val re = """[0-9]{12,20}""".r
    val iterator = re.findAllMatchIn(input)
    val numList: List[String] = iterator.toList.map(x => x.toString())
    val numMap: Map[String, String] = numList.map(x => x -> maskNumber(x)).toMap

    @tailrec
    def loop(n: Int, acc: String): String = {
      if (n < 0) acc
      else loop(n - 1, acc.replace(numList(n), numMap.getOrElse(numList(n), "")))
    }

    loop(numMap.size - 1, input)
  }

  def maskNumber(number: String): String = {
    val head = number.slice(0, 6)
    val tail = number.slice(number.length - 4, number.length)
    val mask = Array.fill(number.length - 10) {
      "*"
    }.mkString
    number.replace(number, s"$head$mask$tail")
  }
}
