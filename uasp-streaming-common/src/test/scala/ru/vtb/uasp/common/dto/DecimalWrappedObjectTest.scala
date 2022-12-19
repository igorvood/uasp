//package ru.vtb.uasp.common.dto
//
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should
//import ru.vtb.uasp.common.dto.AnyValClassWriter.AnyValSyntax._
//import ru.vtb.uasp.common.utils.json.JsonConverter
//
//import scala.collection.mutable
//
//class DecimalWrappedObjectTest extends AnyFlatSpec with should.Matchers {
//  /** Создание DecimalObj */
//  "DecimalObj value" should "be" in {
//
//    var mapStr: mutable.Map[String, String] = mutable.Map()
//
//    var mapTmp: mutable.Map[String, AnyValClass] = mutable.Map()
//    val bd: AnyValClass = BigDecimal(10.11).toAnyVal
//    val l: AnyValClass = 10L.toAnyVal
//    val i: AnyValClass = 10.toAnyVal
//    val s: AnyValClass = "str".toAnyVal
//    val b: AnyValClass = true.toAnyVal
//    val f: AnyValClass = 1.0f.toAnyVal
//    val d: AnyValClass = 1.0.toAnyVal
//
//    mapTmp += ("l" -> l)
//    mapTmp += ("i" -> i)
//    mapTmp += ("s" -> s)
//    mapTmp += ("b" -> b)
//    mapTmp += ("f" -> f)
//    mapTmp += ("d" -> d)
//
//    mapTmp += ("bd" -> d)
//
//    var modelVectorData = UaspDto(
//      "1",
//      Map(),
//      Map(),
//      Map(),
//      Map(),
//      Map(),
//      Map(),
//      Map(),
//
//      "test",
//      1L
//    )
//
//    println(mapTmp.filter(x => x._2.value.isInstanceOf[Long]).map(y => (y._1, y._2.value)).asInstanceOf[mutable.Map[String, Long]].toMap)
//
//    val map: Map[String, Long] = mapTmp.filter(x => x._2.value.isInstanceOf[Long]).map(y => (y._1, y._2.value)).asInstanceOf[mutable.Map[String, Long]].toMap
//
//    var uasp = UaspDto(
//      "1",
//      mapTmp.filter(x => x._2.value.isInstanceOf[Int]).map(y => (y._1, y._2.value.asInstanceOf[Int])).toMap,
//
//      mapTmp.filter(x => x._2.value.isInstanceOf[Long]).map(y => (y._1, y._2.value.asInstanceOf[Long])).toMap,
//      mapTmp.filter(x => x._2.value.isInstanceOf[Float]).map(y => (y._1, y._2.value.asInstanceOf[Float])).toMap,
//      mapTmp.filter(x => x._2.value.isInstanceOf[Double]).map(y => (y._1, y._2.value.asInstanceOf[Double])).toMap,
//      mapTmp.filter(x => x._2.value.isInstanceOf[BigDecimal]).map(y => (y._1, y._2.value.asInstanceOf[BigDecimal])).toMap,
//      mapTmp.filter(x => x._2.value.isInstanceOf[String]).map(y => (y._1, y._2.value.asInstanceOf[String])).toMap,
//      mapTmp.filter(x => x._2.value.isInstanceOf[Boolean]).map(y => (y._1, y._2.value.asInstanceOf[Boolean])).toMap,
//
//      "test",
//      10L
//    )
//    println(uasp)
//    println(JsonConverter.modelToJson(uasp))
//
//    mapTmp.size should be(6)
//  }
//}
