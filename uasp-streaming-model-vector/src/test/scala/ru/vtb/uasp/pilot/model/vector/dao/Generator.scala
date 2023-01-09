package ru.vtb.uasp.pilot.model.vector.dao

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.json.JsonConverter
import ru.vtb.uasp.pilot.model.vector.constants.ModelVector._

import java.util.UUID
import scala.util.Random


object Generator {

  def createTestObject(customerId: Long, haCaSuffix: String, fields: Map[String, Map[String, String]]
                      ): UaspDto = {

    var mapTmpInt: Map[String, Int] = Map()
    var mapTmpLong: Map[String, Long] = Map()
    var mapTmpFloat: Map[String, Float] = Map()
    var mapTmpDouble: Map[String, Double] = Map()
    var mapTmpBoolean: Map[String, Boolean] = Map()
    var mapTmpString: Map[String, String] = Map()
    var mapTmpDecimal: Map[String, BigDecimal] = Map()

    val uuid: String = UUID.randomUUID().toString
    val process_time: Long = System.currentTimeMillis()

    fields.foreach(x => {
      val fieldName = x._1

      val fieldType = x._2("type")

      fieldType match {
        case STRING => mapTmpString = mapTmpString + (fieldName -> randomString(Random.nextInt(10)))
        case DOUBLE => mapTmpDouble = mapTmpDouble + (fieldName -> Random.nextDouble())
        case LONG => mapTmpLong = mapTmpLong + (fieldName -> Random.nextLong())
        case BIG_DECIMAL => mapTmpDecimal = mapTmpDecimal + (fieldName -> BigDecimal.valueOf(11.11))
        case INT => mapTmpInt = mapTmpInt + (fieldName -> Random.nextInt(100))
        case BOOLEAN => mapTmpBoolean = mapTmpBoolean + (fieldName -> true)
        case FLOAT => mapTmpFloat = mapTmpFloat + (fieldName -> Random.nextFloat())
        case _ =>
      }
    })

    mapTmpString = mapTmpString + ("system-uasp-way-classification" -> "way4-case-2-3")
    mapTmpString = mapTmpString + ("sys-stream" -> haCaSuffix)

    val modelVectorData = UaspDto(
      customerId.toString(),
      mapTmpInt,
      mapTmpLong,
      mapTmpFloat,
      mapTmpDouble,
      mapTmpDecimal,
      mapTmpString,
      mapTmpBoolean,
      uuid,
      process_time
    )

    println(JsonConverter.modelToJson(modelVectorData))
    modelVectorData
  }

  def createTestObjectStatic(customerId: Long, haCaSuffix: String, fields: Map[String, Map[String, String]]
                            ): UaspDto = {

    var mapTmpInt: Map[String, Int] = Map()
    var mapTmpLong: Map[String, Long] = Map()
    var mapTmpFloat: Map[String, Float] = Map()
    var mapTmpDouble: Map[String, Double] = Map()
    var mapTmpBoolean: Map[String, Boolean] = Map()
    var mapTmpString: Map[String, String] = Map()
    var mapTmpDecimal: Map[String, BigDecimal] = Map()

    fields.foreach(x => {
      val fieldName = x._1

      val fieldType = x._2("type")

      fieldType match {
        case STRING => mapTmpString = mapTmpString + (fieldName -> "1")
        case DOUBLE => mapTmpDouble = mapTmpDouble + (fieldName -> 1.0)
        case LONG => mapTmpLong = mapTmpLong + (fieldName -> 1L)
        case BIG_DECIMAL => mapTmpDecimal = mapTmpDecimal + (fieldName -> BigDecimal.valueOf(11.11))
        case INT => mapTmpInt = mapTmpInt + (fieldName -> 1)
        case BOOLEAN => mapTmpBoolean = mapTmpBoolean + (fieldName -> true)
        case FLOAT => mapTmpFloat = mapTmpFloat + (fieldName -> 1.0f)
        case _ =>
      }
    })
    mapTmpString = mapTmpString + ("system-uasp-way-classification" -> "way4-case-2-3")
    mapTmpString = mapTmpString + ("sys-stream" -> haCaSuffix)

    val modelVectorData = UaspDto(
      customerId.toString(),
      mapTmpInt,
      mapTmpLong,
      mapTmpFloat,
      mapTmpDouble,
      mapTmpDecimal,
      mapTmpString,
      mapTmpBoolean,
      "uuid",
      1L
    )

    modelVectorData
  }

  val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private def randomString(n: Int) = (1 to n).map(_ => alpha(Random.nextInt(alpha.length))).mkString

}
