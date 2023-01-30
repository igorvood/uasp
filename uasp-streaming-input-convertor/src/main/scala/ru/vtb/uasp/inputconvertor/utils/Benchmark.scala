package ru.vtb.uasp.inputconvertor.utils

import org.slf4j.LoggerFactory

object Benchmark {
  val logger = LoggerFactory.getLogger(getClass)

  def time[R](mesHeader: String)(block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    logger.info(mesHeader + " elapsed time: " + Math.round((t1 - t0) / 1000) + " microseconds")
    result
  }

}
