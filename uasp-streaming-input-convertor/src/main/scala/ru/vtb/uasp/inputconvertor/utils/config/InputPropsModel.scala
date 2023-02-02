package ru.vtb.uasp.inputconvertor.utils.config

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getPropsFromResourcesFile
import ru.vtb.uasp.common.utils.config.PropertyUtil.{propertyVal, propertyValOptional, s}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.utils.serialization.InputMessageTypeDeserialization
import ru.vtb.uasp.validate.DroolsValidator

import scala.collection.mutable

case class InputPropsModel(
                            serviceName: ServiceDataDto,
                            uaspdtoType: String,
                            consumerProp: FlinkConsumerProperties,
                            outputSink: FlinkSinkProperties,
                            dlqSink: FlinkSinkProperties,

                            readSourceTopicFromBeginning: Boolean,
                            sha256salt: String,
                            messageJsonPath: Option[String],

                            jsonSplitElement: Option[String],

                          ) {

  val droolsValidator = new DroolsValidator(uaspdtoType + "-validation-rules.drl")

  val dtoMap: Map[String, Array[String]] = getPropsFromResourcesFile(s"$uaspdtoType-uaspdto.properties")
    .map(map => map.map(m => (m._1, m._2.split("::"))))
    .getOrElse(throw new IllegalArgumentException(s"unable to read resources file $uaspdtoType-uaspdto.properties"))

  lazy val savepointPref: String = serviceName.serviceNameNoVersion

  lazy val inputMessageTypeDeserialization = new InputMessageTypeDeserialization()


  val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(this)
}

object InputPropsModel extends ConfigurationInitialise[InputPropsModel] {

  val appPrefixDefaultName: String = "uasp-streaming-input-convertor"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): InputPropsModel = InputPropsModel(prf)(allProps, InputPropsModel)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties,
                                                                    configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, InputPropsModel] = {
    for {
      serviceName <- ServiceDataDto.create(s"$prf.service")
      uaspdtoType <- propertyVal[String](s"$prf", "uaspdto.type")(appProps, configurationInitialise, s)
      consumerProp <- FlinkConsumerProperties.create(s"$prf.input")
      outputSink <- FlinkSinkProperties.create(s"$prf.output")
      dlqSink <- FlinkSinkProperties.create(s"$prf.dlq")
      readSourceTopicFromBeginning <- propertyVal[Boolean](s"$prf", "read.source.topic.frombeginning")
      sha256salt <- propertyVal[String](s"$prf", "card.number.sha256.salt")(appProps, configurationInitialise, s)
      messageJsonPath <- propertyValOptional[String](s"$prf", "message.json.path")(appProps, configurationInitialise, s)
      jsonSplitElement <- propertyValOptional[String](s"$prf", "json.split.element")(appProps, configurationInitialise, s)
    } yield
      new InputPropsModel(
        serviceName = serviceName,
        uaspdtoType = uaspdtoType,
        consumerProp = consumerProp,
        outputSink = outputSink,
        dlqSink = dlqSink,
        readSourceTopicFromBeginning = readSourceTopicFromBeginning,
        sha256salt = sha256salt,
        messageJsonPath = messageJsonPath,
        jsonSplitElement = jsonSplitElement

      )
  }
}
