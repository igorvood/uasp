package ru.vtb.uasp.inputconvertor.utils.file

import java.io.IOException
import scala.io.Source

object FileUtils {

  @throws[IOException]
  def getRows: List[String] = {
    var cellValues = List[String]()
    val resource = Source.fromResource("curr.csv")
    val lines: Iterator[String] = resource.getLines
    while (lines.hasNext) {
      val line = lines.next()
      cellValues :+= line
    }
    cellValues
  }


}
