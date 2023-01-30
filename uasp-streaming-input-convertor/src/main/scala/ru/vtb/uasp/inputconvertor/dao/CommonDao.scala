package ru.vtb.uasp.inputconvertor.dao

import org.json4s._
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.TimeZone
import scala.annotation.tailrec

object CommonDao {
  private val logger = LoggerFactory.getLogger(getClass)
  implicit val formats: Formats = DefaultFormats.disallowNull

  def getMap[T](mapKey: String, mapValue: => T): Map[String, T] = {
    try {
      val res: T = mapValue
      logger.info("mapKey: " + mapKey + ", res: " + res + ", " + Map(mapKey -> res))
      Map(mapKey -> res)
    } catch {
      case e: Throwable =>
        //logger.info("key: " + mapKey + ", " + e.getMessage)
        logger.warn("key: " + mapKey + ", " + e.getMessage)
        Map[String, T]()
    }
  }

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

  def maskNumber(number: String) = {
    val head = number.slice(0, 6)
    val tail = number.slice(number.length - 4, number.length)
    val mask = Array.fill(number.length - 10) {
      "*"
    }.mkString
    number.replace(number, s"$head$mask$tail")
  }
}
