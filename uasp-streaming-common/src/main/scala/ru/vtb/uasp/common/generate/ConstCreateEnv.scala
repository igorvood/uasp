package ru.vtb.uasp.common.generate

import ru.vtb.uasp.common.generate.dto.{CreationEnvProp, PlaceholderDto, Profile, StandDTO}

import scala.collection.immutable



object ConstCreateEnv {

  val placeholders: Map[String, PlaceholderDto] = Map(
    ".bootstrap.servers" -> PlaceholderDto("${BOOTSTRAP_SERVERS}"),
    ".ssl.key.password" -> PlaceholderDto("${DSO_KAFKA_SSL_KEY_PASSWORD}"),
    ".ssl.keystore.location" -> PlaceholderDto("${KEYSTORE_LOCATION}"),
    ".ssl.keystore.password" -> PlaceholderDto("${DSO_KAFKA_SSL_KEYSTORE_PASSWORD}"),
    ".ssl.truststore.password" -> PlaceholderDto("${DSO_KAFKA_SSL_TRUSTSTORE_PASSWORD}"),
    ".ssl.truststore.location" -> PlaceholderDto("${TRUSTSTORE_LOCATION}"),
    ".ssl.truststore.location" -> PlaceholderDto("${TRUSTSTORE_LOCATION}"),
    ".security.protocol" -> PlaceholderDto("SSL"),
    ".sync.parallelism" -> PlaceholderDto("PARALLELISM", upToHead = true),
    ".service.name" -> PlaceholderDto("PROFILE_NAME", calcNewValue = Some("${PROFILE_NAME}_${SERVICE_NAME}"), upToHead = true)

  )

  val rootFolder = "conf-deployment"


  private def headConst(clazz: Class[_]) =
    s"""MAIN_CLASS=${clazz.getName.replace("$","")}
      |
      |PROGRAMARGS=""".stripMargin

  private val standsList: List[StandDTO] = List(
    StandDTO("dso", "dev_", "dev_", "DSO", "DSO"),
    StandDTO("ift", "dev_", "ift_", "DSO", "IFT"),
    StandDTO("nt", "dev_", "nt_", "DSO", "NT"),
    StandDTO("p0", "dev_", "p0_", "DSO", "P0"),
    StandDTO("real", "dev_", "rr_", "DSO", "RR"),
    StandDTO("future", "dev_", "dev_future_", "DSO", "DSO"),
  )

//  val serviceName = "generated-mdm-enrichment"

  def generatorPropList(implicit prfList: List[Profile], clazz: Class[_]): immutable.Seq[GeneratorProp] = List( CreationEnvProp(prefix = "\"--", postFix = " \"", propertyDelimer = "`\n`", keyValueDelimer = " ", headConst(clazz), "env"))
    .flatMap(crProp => standsList.map(stand => stand -> crProp))
    .flatMap(tr => prfList.map(prf => GeneratorProp(tr._2, tr._1, prf)))


}
