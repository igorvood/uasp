package ru.vtb.uasp.pilot.model.vector.service

import org.apache.flink.api.common.functions.RichMapFunction
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsObject


class SortJsonFieldsService extends RichMapFunction[JsObject, JsObject] {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)


  override def map(inMsg: JsObject): JsObject = {
    try {
     JsObject(inMsg.fields
       .sortBy(_._1)
     )
    } catch {
      case e: Throwable => e match {
        case _ =>
          logger.error(e.getMessage)
          throw new RuntimeException(e)
      }
    }
  }
}
