package ru.vtb.uasp.inputconvertor.utils

import ru.vtb.uasp.inputconvertor.utils.file.FileUtils

import scala.collection.JavaConversions.asJavaCollection
import scala.collection.mutable

object CurrencyConverter {

  val rowsList: List[String] = FileUtils.getRows
  var currencyMap = mutable.Map[String, String]()

  rowsList.forEach(r => {
    val row = r.split(";")
    currencyMap.put(row(0), row(1))

  })

  def returnAlphaCode(currNumCode: String): String = {
    if (currencyMap.contains(currNumCode))
      currencyMap(currNumCode) else ""
  }

}
