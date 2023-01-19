package ru.vtb.uasp.vector.util

import org.scalatest.flatspec.AnyFlatSpec


class FileUtilsTest extends AnyFlatSpec {
  val path = "src/main/resources/cases"

  "FileUtils.getListOfFiles " should "be List[String]" in {
    assertResult(10)(FileUtils.getListOfFiles(path).size)
  }

  "FileUtils.getListFileContentFromPath " should "be List[String]" in {
    assertResult(10)(FileUtils.getListFileContentFromPath(path).size)
  }
}
