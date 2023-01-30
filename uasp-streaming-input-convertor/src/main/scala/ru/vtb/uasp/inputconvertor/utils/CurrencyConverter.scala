package ru.vtb.uasp.inputconvertor.utils

import ru.vtb.uasp.inputconvertor.utils.file.FileUtils
import scala.collection.JavaConversions.asJavaCollection
import scala.collection.mutable

object CurrencyConverter {

  var CurrencyMap = mutable.Map[String, String]()

  val rowsList: List[String] = FileUtils.getRows

  rowsList.forEach(r => {
    val row = r.split(";")
    CurrencyMap.put(row(0), row(1))

  })

  def returnAlphaCode(currNumCode: String): String = {
    if (CurrencyMap.contains(currNumCode))
      CurrencyMap(currNumCode) else ""
  }

}
