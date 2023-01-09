package ru.vtb.uasp.pilot.model.vector.dao

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.pilot.model.vector.constants.ModelVector._

import java.time.Instant
import scala.collection.mutable


class ModelVectorDao extends Serializable {

  def map(inMsg: UaspDto, fieldsMap: Map[String, Map[String, String]]): UaspDto = {

    val mapString: mutable.Map[String, String] = mutable.Map()
    val mapLong: mutable.Map[String, Long] = mutable.Map()
    val mapInt: mutable.Map[String, Int] = mutable.Map()
    val mapFloat: mutable.Map[String, Float] = mutable.Map()
    val mapBoolean: mutable.Map[String, Boolean] = mutable.Map()
    val mapDecimal: mutable.Map[String, BigDecimal] = mutable.Map()
    val mapDouble: mutable.Map[String, Double] = mutable.Map()

    fieldsMap.foreach(field => {
      val modelVectorFieldName = field._1
      val cftFieldName = field._2(CFT_FIELD_NAME)
      val caFieldName = field._2(CA_FIELD_NAME)
      val w4FieldName = field._2(W4_FIELD_NAME)
      val pfFieldName = field._2(PROFILE_FIELD_NAME)
      val wdFieldName = field._2(WITHDRAW_FIELD_NAME)
      val fieldType = field._2(TYPE)
      val dttmFormat = field._2.getOrElse(DTTM_FORMAT, "")

      fieldType match {
        case STRING => mapString += (modelVectorFieldName ->
          inMsg.dataString.getOrElse(cftFieldName,
            inMsg.dataString.getOrElse(caFieldName,
              inMsg.dataString.getOrElse(w4FieldName,
                inMsg.dataString.getOrElse(pfFieldName,
                inMsg.dataString.getOrElse(pfFieldName,
                inMsg.dataString.getOrElse(wdFieldName,
                  null)))))))
        case INT => mapInt += (modelVectorFieldName ->
          inMsg.dataInt.getOrElse(cftFieldName,
            inMsg.dataInt.getOrElse(caFieldName,
              inMsg.dataInt.getOrElse(w4FieldName,
                inMsg.dataInt.getOrElse(pfFieldName,
                inMsg.dataInt.getOrElse(wdFieldName,
                  1))))))
          //TODO refactor date with date format
        case LONG => if (dttmFormat.nonEmpty) {
          mapString += (modelVectorFieldName -> (Instant.ofEpochMilli(
            inMsg.dataLong.getOrElse(cftFieldName,
              inMsg.dataLong.getOrElse(caFieldName,
                inMsg.dataLong.getOrElse(w4FieldName,
                  inMsg.dataLong.getOrElse(pfFieldName,
                  inMsg.dataLong.getOrElse(wdFieldName,
                    System.currentTimeMillis())))))+3*60*60*1000.toLong))
            .toString.replaceAll("T", " ").substring(0, 19))
        }

        else mapLong += (modelVectorFieldName ->
          inMsg.dataLong.getOrElse(cftFieldName,
            inMsg.dataLong.getOrElse(caFieldName,
              inMsg.dataLong.getOrElse(pfFieldName,
                inMsg.dataLong.getOrElse(w4FieldName,
                inMsg.dataLong.getOrElse(wdFieldName,
                  0))))))

        case FLOAT => mapFloat += (modelVectorFieldName ->
          inMsg.dataFloat.getOrElse(cftFieldName,
            inMsg.dataFloat.getOrElse(caFieldName,
              inMsg.dataFloat.getOrElse(w4FieldName,
                inMsg.dataFloat.getOrElse(pfFieldName,
                inMsg.dataFloat.getOrElse(wdFieldName,
                  0.0f))))))
        case DOUBLE => mapDouble += (modelVectorFieldName ->
          inMsg.dataDouble.getOrElse(cftFieldName,
            inMsg.dataDouble.getOrElse(caFieldName,
              inMsg.dataDouble.getOrElse(w4FieldName,
                inMsg.dataDouble.getOrElse(pfFieldName,
                inMsg.dataDouble.getOrElse(wdFieldName,
                  0.0))))))
        case BOOLEAN => mapBoolean += (modelVectorFieldName ->
          inMsg.dataBoolean.getOrElse(cftFieldName,
            inMsg.dataBoolean.getOrElse(caFieldName,
              inMsg.dataBoolean.getOrElse(w4FieldName,
                inMsg.dataBoolean.getOrElse(pfFieldName,
                inMsg.dataBoolean.getOrElse(wdFieldName,
                  false))))))
        case BIG_DECIMAL => mapDecimal += (modelVectorFieldName ->
          inMsg.dataDecimal.getOrElse(cftFieldName,
            inMsg.dataDecimal.getOrElse(caFieldName,
              inMsg.dataDecimal.getOrElse(w4FieldName,
                inMsg.dataDecimal.getOrElse(pfFieldName,
                inMsg.dataDecimal.getOrElse(wdFieldName,
                  BigDecimal(0.0)))))))
      }
    }
    )

    mapString.put(CLASSIFICATION, inMsg.dataString.getOrElse(CLASSIFICATION, ""))
    val result = inMsg
      .copy(
        dataLong = mapLong.toMap,
        dataInt = mapInt.toMap,
        dataDouble = mapDouble.toMap,
        dataDecimal = mapDecimal.toMap,
        dataString = mapString.toMap,
        dataFloat = mapFloat.toMap,
        dataBoolean = mapBoolean.toMap)
    result

  }
}
