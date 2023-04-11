package ru.vtb.uasp.mdm.enrichment.perfomance

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json
import ru.vtb.uasp.mdm.enrichment.perfomance.ExtractPerformanceTest.{inJsonStrValid, packageServiceInDto}
import ru.vtb.uasp.mdm.enrichment.perfomance.NodeTest.metaJsonProperty
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json.NodeJsonMeta

import java.util.Date
import scala.collection.immutable

class ExtractPerformanceTest extends AnyFlatSpec {

  private val cnt: Int = 10000

  private val perMs = 1000

  private val jsons: immutable.Seq[String] = (1 to cnt)
    .map(cnt => packageServiceInDto.copy(OPERATION_ID = cnt.toString))
    .map(dto => inJsonStrValid(dto))

  behavior of "PerformanceTest"

  it should " to map string " in {

    val meta = NodeJsonMeta(metaJsonProperty)

    val duration = performanceTest(
      "MAPSTRING first",
      {
        json =>
          val value = Json.parse(json)
          meta.extract(value)
      }
    )

    assert(duration < 40000)

    val duration2 = performanceTest("MAPSTRING_2",
      {
        json =>
          val value = Json.parse(json)
          meta.extract(value)
      }
    )

    assert(duration2 < 40000)
  }

  private def performanceTest(prefix: String, value: String => Unit): Double = {
    val beginTime = new Date().getTime

    jsons
      .foreach { json =>
        value(json)
      }

    val endTime = new Date().getTime

    val totalMs = (endTime - beginTime).toDouble
    val l = (cnt / totalMs) * perMs


    println(s"$prefix DTO per ${perMs} millisecond $l rec. Total ${totalMs / 1000} seconds")

    l
  }

}

object ExtractPerformanceTest {
  val packageServiceInDto: FirstLevelDto = FirstLevelDto(OPERATION_ID = "1", MDM_ID = "2", EVENT_DTTM = "2022-06-27 16:11:21", NEW_PACKAGE = "4", OLD_PACKAGE = "5")


  def inJsonStrValid(implicit packageServiceInDto: FirstLevelDto): String = Json.stringify(Json.toJsObject(packageServiceInDto))
}