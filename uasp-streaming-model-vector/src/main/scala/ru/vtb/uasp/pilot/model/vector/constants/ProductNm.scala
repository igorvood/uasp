package ru.vtb.uasp.pilot.model.vector.constants

import ru.vtb.uasp.pilot.model.vector.Util.file.FileUtils

import scala.collection.JavaConversions.asJavaCollection
import scala.collection.mutable


object ProductNm {

  var ProductNmMap = mutable.Map[String, String]()

  val rowsList: List[String] = FileUtils.getRows

  rowsList.forEach(r => {
    val row = r.split(";")
    ProductNmMap.put(row(0), row(1))

  })

  def returnProductNm(contract_card_type_cd: String): String = {
    if (ProductNmMap.contains(contract_card_type_cd))
      ProductNmMap(contract_card_type_cd) else ""
  }

}