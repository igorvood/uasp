package ru.vtb.uasp.inputconvertor.utils.config

//import ru.vtb.uasp.common.utils.config.ConfigUtils.{getDtoMap, getPropsFromMap}

import java.util.Properties

@deprecated
case class InputPropsModel(config: Map[String, String], appPrefix: String = "") {
  @deprecated
  val SERVICE_TYPE = "input-convertor"
  @deprecated
  val SERVICE_TYPE_SYS = "input-convertor-sys"
  @deprecated
  val instanceConfName = if (appPrefix.equals("")) config.getOrElse(SERVICE_TYPE + ".instance.conf.name", "")
    else appPrefix
  @deprecated
  val appPrefixName: String = if (instanceConfName.isEmpty) SERVICE_TYPE else SERVICE_TYPE + "." + instanceConfName
  @deprecated
  val sysPrefixName: String = SERVICE_TYPE_SYS + "." + instanceConfName

  val appServiceName: String = config.getOrElse(s"$SERVICE_TYPE.service.name", "")
  val appUaspdtoType: String = config.getOrElse(s"$appPrefixName.uaspdto.type", "")
  val appInputTopicName: String = config.getOrElse(s"$appPrefixName.input.topic.name", "")
  val appInputTopicGroupId: String = config.getOrElse(s"$appPrefixName.input.topic.group.id", "")
  val appOutputTopicName: String = config.getOrElse(s"$appPrefixName.output.topic.name", "")
  val appDlqTopicName: String = config.getOrElse(s"$appPrefixName.dlq.topic.name", "")
  val appJsonSchemaTopicName: String = config.getOrElse(s"$appPrefixName.json.schema.topic.name", "")
  val appJsonSchemaTopicGroupId: String = config.getOrElse(s"$appPrefixName.json.schema.topic.group.id", "")
  val appAvroSchemaTopicName: String = config.getOrElse(s"$appPrefixName.avro.schema.topic.name", "")
  val appSchemaName: String = config.getOrElse(s"$appPrefixName.schema.name", "")
  val appUseAvroSerialization: String = config.getOrElse(s"$appPrefixName.use.avro.serialization", "")
  val appSavepointPref: String = config.getOrElse(s"$appPrefixName.savepoint.pref", "InputConvertor")
  val dtoMap: Map[String, Array[String]] = Map() //getDtoMap(appUaspdtoType + "-uaspdto.properties")
  val kafkaProducerPoolSize: Int = config.getOrElse(s"$appPrefixName.kafka.producer.pool.size", "5").toInt

//  val appWriteToSink: String = config.getOrElse(s"$appPrefixName.write.to.sink", "Y") // уточнить
  val appJsonSchemaVersion: String = config.getOrElse(s"$appPrefixName.json.schema.version", "")
  val appStreamCheckpointTimeMilliseconds: String = config.getOrElse(s"$sysPrefixName.stream.checkpoint.time.milliseconds", "10000")
  val appReadSourceTopicFrombeginning: String = config.getOrElse(s"$sysPrefixName.read.source.topic.frombeginning", "Y")
  val SHA256salt: String = config.getOrElse(s"$sysPrefixName.card.number.sha256.salt", "")

  val commonKafkaProps: Properties = new Properties()
//    getPropsFromMap(
//    config.filterKeys(key => key.startsWith(sysPrefixName))
//      .map {
//        case (k, v) => (k.replace(s"$sysPrefixName.", ""), v)
//      }
//  )
//  val appWriteToSinkIsY: Boolean = appWriteToSink.toUpperCase == "Y"
  val appUseAvroSerializationIsY: Boolean = appUseAvroSerialization.toUpperCase == "Y"
}