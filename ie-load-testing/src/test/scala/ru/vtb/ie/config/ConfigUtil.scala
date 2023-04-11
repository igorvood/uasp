package ru.vtb.ie.config

import ru.vtb.ie.base.IDEPathHelper

object ConfigUtil {

  def getConf(sysEnv: Map[String, String]): Config = {
    println("sysEnv: " + sysEnv.toString())
    val config: Config = Config(
      userCount = sysEnv.getOrElse("userCount", "1").toInt,
      countSeconds = sysEnv.getOrElse("countSeconds", "1").toInt,
      baseUrlIE = sysEnv.getOrElse("baseUrlIE", "http://ie.ds5-genr02-pimc-streaming-dev-new.apps.ds5-genr02.corp.dev.vtb"),
      baseUrlTarantool = sysEnv.getOrElse("baseUrlTarantool", "http://d5uasp-apc021lk.corp.dev.vtb:8083"),
      topicSmallJson = sysEnv.getOrElse("topicSmallJson", "dev_ivr__uasp_realtime__operations__json"),
      topicBigJson = sysEnv.getOrElse("topicBigJson", "dev_ivr__uasp_realtime__model_vector__json"),
      //      topicSmallJson = sysEnv.getOrElse("topicSmallJson", "dev_feature_ivr__uasp_realtime__operations__json"),
      //      topicBigJson = sysEnv.getOrElse("topicBigJson", "dev_feature_ivr__uasp_realtime__model_vector__json"),

      bootstrapServers = sysEnv.getOrElse("bootstrapServers", "d5uasp-apc002lk.corp.dev.vtb:9092,d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc004lk.corp.dev.vtb:9092"),
      sslTruststoreLocation = sysEnv.getOrElse("sslTruststoreLocation", IDEPathHelper.mavenResourcesDirectory.toString + "\\kafka-trust.pfx"),
      sslTruststorePassword = sysEnv.getOrElse("sslTruststorePassword", "*"),
      sslKeystoreLocation = sysEnv.getOrElse("sslKeystoreLocation", IDEPathHelper.mavenResourcesDirectory.toString + "\\APD00.13.01-USBP-kafka-cluster-uasp.pfx"),
      sslKeystorePassword = sysEnv.getOrElse("sslKeystorePassword", "*"),
      sslKeyPassword = sysEnv.getOrElse("sslKeyPassword", "*"),
      isPrintRqBody = sysEnv.getOrElse("isPrintRqBody", "false").toBoolean,
      isPrintRspTarantoolBody = sysEnv.getOrElse("isPrintRspBody", "true").toBoolean,
      isPrintRspIEBody =  sysEnv.getOrElse("isPrintRspIEBody", "false").toBoolean,
      checkTarantoolRest = sysEnv.getOrElse("checkTarantoolRest", "true").toBoolean,
      isPrintSession = sysEnv.getOrElse("isPrintSession", "false").toBoolean,
    )
    println("config: " + config.toString())
    config
  }
}
