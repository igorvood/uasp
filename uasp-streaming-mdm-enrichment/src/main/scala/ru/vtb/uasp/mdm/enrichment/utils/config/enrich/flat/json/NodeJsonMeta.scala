package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json

import play.api.libs.json._

import scala.annotation.tailrec
import scala.collection.immutable

sealed trait NodeJsonMeta {

  val path: List[String]

  lazy val pathStr: String = path.mkString(".")

  def extract(jsVal: JsValue): Either[String, Map[String, String]]


}

case class FieldJsonMeta(typeF: TypeMeta, path: List[String]) extends NodeJsonMeta {
  override def extract(json: JsValue): Either[String, Map[String, String]] = {
    val maybeData = (json, typeF) match {
      case (JsBoolean(b), BooleanMeta()) => Right(Some(pathStr -> b.toString))
      case (JsNumber(n), NumberMeta()) => Right(Some(pathStr -> n.toString))
      case (JsString(s), StringMeta()) => Right(Some(pathStr -> s))
      case (JsNull, _) => Right(None)
      case (js, m) => Left(s"Non compatible data type for path $pathStr json val => $js, meta data type => $m")
    }

    val mayBeOk = maybeData
      .map(p => p.map(p1 => Map(p1)).getOrElse(Map()))
    mayBeOk
  }
}

case class ObjectJsonMeta(meta: Map[String, NodeJsonMeta], path: List[String]) extends NodeJsonMeta {
  override def extract(jsVal: JsValue): Either[String, Map[String, String]] = {

    val unionList = jsVal match {
      case JsObject(mapVal) =>
        meta.flatMap { m =>
          mapVal.get(m._1)
            .map(j => m._2.extract(j))
        }
    }

    @tailrec
    def union(listMap: immutable.Iterable[Map[String, String]], res: Map[String, String]): Map[String, String] = {
      listMap match {
        case Nil => res
        case x :: xs => union(xs, res ++ x)
      }
    }

    val okData = unionList
      .collect { case q: Right[String, Map[String, String]] => q.value }

    val eitherRes = if (okData.size == unionList.size) {
      Right(union(okData, Map()))
    } else {
      val err = unionList
        .collect { case q: Left[String, Map[String, String]] => q.value }
        .mkString(",")
      Left(err)
    }
    eitherRes


  }
}


object NodeJsonMeta {

  def apply(m: Map[String, String]): NodeJsonMeta = {

    val stringsToString: Map[Array[String], String] = m.map { ent =>
      val strings = ent._1.split('.')
      strings -> ent._2
    }

    convert(stringsToString, List())

  }


  private def convert(meta: Map[Array[String], String], path: List[String]): NodeJsonMeta = {

    val groupedByFirstNodeOfPath: Map[String, Map[Array[String], String]] = meta
      .groupBy(k => k._1(0))
    val groupedByFirstLevel =
      groupedByFirstNodeOfPath
        .map { d =>
          val key = d._1
          val map = d._2

          map.size match {
            case 1 => key -> FieldJsonMeta(TypeMeta(map.toList.head._2) /*d._2.toList.head._2*/ , path :+ key)
            case _ =>
              val obj = d._2.map(q => q._1.tail -> q._2)
              key -> convert(obj, path :+ key)
          }
        }
    ObjectJsonMeta(groupedByFirstLevel, path)
  }

}
