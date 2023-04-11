package ru.vtb.uasp.streaming.mdm.enrichment.itest.service

import ru.vtb.uasp.common.dto.UaspDto

object CheckService extends App {


  def equalsModelVector(localValue: UaspDto, clusterValue: UaspDto): Boolean = {
    // Сколько времени прошло с момента последней транзакции сравнивать не имеет смысла, так как момент обработки
    // локально и на кластере ни когда не совпадёт
    val dataLongNew: scala.collection.mutable.Map[String, Long] = scala.collection.mutable.Map(localValue.dataLong.toSeq: _*)
    dataLongNew.put("last_oper_21", clusterValue.dataLong("last_oper_21"))

    // Во входящем json отсутствуют данные поля, поэтому не сравниваем
    val dataStringNew: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map(localValue.dataString.toSeq: _*)
    dataStringNew.put("actDate", clusterValue.dataString("actDate"))
    dataStringNew.put("actId", clusterValue.dataString("actId"))

    if (localValue.copy(dataLong = dataLongNew.toMap[String, Long], dataString = dataStringNew.toMap[String, String],
      process_timestamp = 0, uuid = "") == clusterValue.copy(process_timestamp = 0, uuid = "")) {
      true
    } else {
      false
    }
  }

}
