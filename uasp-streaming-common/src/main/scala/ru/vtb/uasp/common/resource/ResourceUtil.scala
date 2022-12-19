package ru.vtb.uasp.common.resource

import scala.io.Source

object ResourceUtil {

  def getTextOfFile(path: String): String = {
    val source = Source.fromURL(getClass.getResource(path))
    try {
      source.getLines().mkString
    } finally {
      if (source != null) {
        source.close()
      }
    }
  }

}
