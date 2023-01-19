package ru.vtb.uasp.vector.util

import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.vector.UaspStreamingModelVector.logger
import ru.vtb.uasp.vector.dsl.RuleParser.getClass

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}


object FileUtils {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def getListOfFiles(dir: String): List[String] = {
    /*
        new File(dir)
      .listFiles
      .filter(_.isFile)
      .map(_.getPath)
      .toList
     */

    //TODO ONLY FOR TEST
    Try {
      val file = new File(dir)

      if (file.isDirectory) {
        new File(dir)
          .listFiles
          .filter(_.isFile)
          .map(_.getPath)
          .toList
      } else {
        new File("cases")
          .listFiles
          .filter(_.isFile)
          .map(_.getPath)
          .toList
      }
    }  match {
      case Failure(exception) =>
        logger.error("error:" + exception.toString + exception.getCause)
        //TODO DELETE ON:LY FOR TEST!!!!!!!!!!!!!!!!!!!!!!!!
        List(
          "cases/Case29.json",
          "cases/Case38.json",
          "cases/Case39New.json",
          "cases/Case44.json",
          "cases/Case48.json",
          "cases/Case51.json",
          "cases/Case56.json",
          "cases/Case57.json",
          "cases/Case71.json",
          "cases/Case8.json",
        )
      case Success(res) => res
    }

  }

  def getListFileContentFromPath(path: String): List[String] = {
    Try {
      FileUtils.getListOfFiles(path)
        .map(a => {
          var cellValues = List[String]()
          val resource = Source.fromURI(new File(a).toURI)
          val lines: Iterator[String] = resource.getLines
          while (lines.hasNext) {
            val line = lines.next()
            cellValues :+= line
          }

          cellValues.mkString
        })
    } match {
      case Failure(exception) =>
        logger.error("error1:" + exception.toString + exception.getCause)
        FileUtils.getListOfFiles(path)
          .map(a => {
            var cellValues = List[String]()
            val resource = Source.fromResource(a)
            val lines: Iterator[String] = resource.getLines
            while (lines.hasNext) {
              val line = lines.next()
              cellValues :+= line
            }
            cellValues.mkString
          })

      case Success(res) => res
    }
  }

}
