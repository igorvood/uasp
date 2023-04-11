package ru.vtb.uasp.streaming.mdm.enrichment.itest.base

import java.nio.file.{Path, Paths}

object IDEPathHelper {

  def mavenSourcesDirectory: Path = mavenSrcTestDirectory.resolve("scala")
  def mavenResourcesDirectory: Path = mavenSrcTestDirectory.resolve("src/test/resources")
  def mavenBinariesDirectory: Path = mavenTargetDirectory.resolve("test-classes")
  def resultsDirectory: Path = mavenTargetDirectory.resolve("gatling")
  def recorderConfigFile: Path = mavenResourcesDirectory.resolve("recorder.conf")
  private def projectRootDir = Paths.get(getClass.getClassLoader.getResource("gatling.conf").toURI).getParent.getParent.getParent
  private def mavenTargetDirectory = projectRootDir.resolve("target")
  private def mavenSrcTestDirectory = projectRootDir.resolve("src").resolve("test")
}
