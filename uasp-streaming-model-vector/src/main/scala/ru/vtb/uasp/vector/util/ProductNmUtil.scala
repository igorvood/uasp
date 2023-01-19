package ru.vtb.uasp.vector.util

import java.io.IOException
import scala.collection.mutable
import scala.collection.JavaConversions.asJavaCollection
import scala.io.Source

object ProductNmUtil {
  var ProductNmMap: mutable.Map[String, String] = mutable.Map[String, String]()

  val rowsList: List[String] = getRows

  rowsList.forEach(r => {
    val row = r.split(";")
    ProductNmMap.put(row(0), row(1))

  })

  @throws[IOException]
  def getRows: List[String] = {
    var cellValues = List[String]()
    val resource = Source.fromResource("product_nm.csv")
    val lines: Iterator[String] = resource.getLines
    while (lines.hasNext) {
      val line = lines.next()
      cellValues :+= line
    }
    cellValues
  }

  def returnProductNm(contract_card_type_cd: String): String = {
    if (ProductNmMap.contains(contract_card_type_cd))
      ProductNmMap(contract_card_type_cd) else ""
  }

}
