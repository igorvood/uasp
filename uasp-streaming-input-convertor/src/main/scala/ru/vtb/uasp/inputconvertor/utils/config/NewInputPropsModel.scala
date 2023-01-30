package ru.vtb.uasp.inputconvertor.utils.config

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.PropertyUtil.{propertyVal, propertyValOptional, s}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.inputconvertor.utils.serialization.InputMessageTypeDeserialization

import scala.collection.mutable

case class NewInputPropsModel(
                               appServiceName: ServiceDataDto, //= config.getOrElse(s"$SERVICE_TYPE.service.name", "")
                               appUaspdtoType: String, //= config.getOrElse(s"$appPrefixName.uaspdto.type", "")
                               appInputTopicName: FlinkConsumerProperties, // = config.getOrElse(s"$appPrefixName.input.topic.name", "")
                               appOutputTopicName: FlinkSinkProperties, //= config.getOrElse(s"$appPrefixName.output.topic.name", "")
                               appDlqTopicName: FlinkSinkProperties, //= config.getOrElse(s"$appPrefixName.dlq.topic.name", "")
                               appUseAvroSerialization: Boolean, //= config.getOrElse(s"$appPrefixName.use.avro.serialization", "")
                               appSavepointPref: String, //= config.getOrElse(s"$appPrefixName.savepoint.pref", "InputConvertor")
                               dtoMap: Map[String, Array[String]], //= getDtoMap(appUaspdtoType + "-uaspdto.properties")

                               appReadSourceTopicFrombeginning: Boolean, // = config.getOrElse(s"$sysPrefixName.read.source.topic.frombeginning", "Y")
                               SHA256salt: String, // = config.getOrElse(s"$sysPrefixName.card.number.sha256.salt", "")
                               messageJsonPath: Option[String],

                               jsonSplitElement: Option[String],

                             ) {

  lazy val inputMessageTypeDeserialization = new InputMessageTypeDeserialization()
}

object NewInputPropsModel extends ConfigurationInitialise[NewInputPropsModel] {

  val appPrefixDefaultName: String = "uasp-streaming-input-convertor"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): NewInputPropsModel = NewInputPropsModel(prf)(allProps, NewInputPropsModel)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties,
                                                                    configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, NewInputPropsModel] = {
    for {
      appServiceName <- ServiceDataDto.create(s"$prf.service")
      appUaspdtoType <- propertyVal[String](s"$prf", "uaspdto.type")(appProps, configurationInitialise, s)
      appInputTopicName <- FlinkConsumerProperties.create(s"$prf.input.topic.name")
      appOutputTopicName <- FlinkSinkProperties.create(s"$prf.output.topic.name")
      appDlqTopicName <- FlinkSinkProperties.create(s"$prf.dlq.topic.name")
      appUseAvroSerialization <- propertyVal[Boolean](s"$prf", "use.avro.serialization")
      appSavepointPref <- Right("asd")
      dtoMap <- Right(Map[String, Array[String]]())
      appReadSourceTopicFrombeginning <- propertyVal[Boolean](s"$prf", "read.source.topic.frombeginning")
      sHA256salt <- propertyVal[String](s"$prf", "card.number.sha256.salt")(appProps, configurationInitialise, s)
      messageJsonPath <- propertyValOptional[String](s"$prf", "message.json.path")(appProps, configurationInitialise, s)
      jsonSplitElement <- propertyValOptional[String](s"$prf", "json.split.element")(appProps, configurationInitialise, s)
      // getOrElse("input-convertor.json.split.element", "")

    } yield
      new NewInputPropsModel(
        appServiceName = appServiceName,
        appUaspdtoType = appUaspdtoType,
        appInputTopicName = appInputTopicName,
        appOutputTopicName = appOutputTopicName,
        appDlqTopicName = appDlqTopicName,
        appUseAvroSerialization = appUseAvroSerialization,
        appSavepointPref = appSavepointPref,
        dtoMap = dtoMap,
        appReadSourceTopicFrombeginning = appReadSourceTopicFrombeginning,
        SHA256salt = sHA256salt,
        messageJsonPath = messageJsonPath,
        jsonSplitElement = jsonSplitElement

      )
  }
}
