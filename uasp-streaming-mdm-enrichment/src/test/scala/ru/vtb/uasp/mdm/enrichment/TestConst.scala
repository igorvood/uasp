package ru.vtb.uasp.mdm.enrichment

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.utils.config.kafka.{KafkaCnsProperty, KafkaPrdProperty}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._

import java.util.Properties

object TestConst {

  def properties = {
    val properties1 = new Properties()

    properties1.put("bootstrap.servers", "asd")
    properties1.put("group.id", "asd")
    properties1
  }

  lazy val mainStreamProperty = new MainEnrichProperty(
    fromTopic = FlinkConsumerProperties(calcTopicName("From" + "mainStreamName"), KafkaCnsProperty(properties)),
    toTopicProp = FlinkSinkProperties(calcTopicName("To" + "mainStreamName"), KafkaPrdProperty(properties), None, None, None),
    dlqTopicProp = Some(FlinkSinkProperties(calcTopicName("Dlq" + "mainStreamName"), KafkaPrdProperty(properties), None, None, None)),
  )
  lazy val globalIdStreamProperty = new GlobalIdEnrichProperty(
    fromTopic = FlinkConsumerProperties(calcTopicName("FromGlobal"), KafkaCnsProperty(properties)),
    dlqTopicProp = Some(FlinkSinkProperties(calcTopicName("dlqGlobal"), KafkaPrdProperty(properties), None, None, None)),
    globalEnrichFields = EnrichFields(
      fromFieldName = s"global_id",
      fromFieldType = "String",
      toFieldName = s"global_id",
      isOptionalEnrichValue = false,
    ),
    keySelectorMain = KeySelectorProp(true),
    keySelectorEnrich = KeySelectorProp(true),
    fields = List[EnrichFields](
      EnrichFields(
        fromFieldName = s"StringKey",
        fromFieldType = "String",
        toFieldName = s"StringValue",
        isOptionalEnrichValue = true,
      ),

    ),
    inputDataFormat = UaspDtoFormat,
    List.empty
  )
  @deprecated
  lazy val mainStreamName1: String = mainStreamProperty.getClass.getSimpleName
  @deprecated
  lazy val globalIdStreamName: String = globalIdStreamProperty.getClass.getSimpleName
  @deprecated
  lazy val hypothecStreamName: String = commonStreamProperty.getClass.getSimpleName
  lazy val enrichPropertyMap = new AllEnrichProperty(
    commonEnrichProperty = Some(commonStreamProperty),
    globalIdEnrichProperty = Some(globalIdStreamProperty),
    mainEnrichProperty = mainStreamProperty
  )
  private lazy val commonStreamProperty = new CommonEnrichProperty(
    fromTopic = FlinkConsumerProperties(calcTopicName("FromHypotec"), KafkaCnsProperty(properties)),
    dlqTopicProp = Some(FlinkSinkProperties(calcTopicName("dlqGHypotec"), KafkaPrdProperty(properties), None, None, None)),
    keySelectorMain = KeySelectorProp(true),
    keySelectorEnrich = KeySelectorProp(true),
    fields = List[EnrichFields](EnrichFields(
      fromFieldName = s"is_mortgage",
      fromFieldType = "Boolean",
      toFieldName = s"is_mortgage",
      isOptionalEnrichValue = true,
    )

    ),
    inputDataFormat = UaspDtoFormat,
    List.empty
  )

  def calcTopicName(prf: String) = s"$prf Topic"

  def mainStreamName = MsgType(MainEnrichProperty.getClass.getSimpleName.replace("$", ""))

  def globalStreamName = MsgType(GlobalIdEnrichProperty.getClass.getSimpleName.replace("$", ""))

  def commonStreamName = MsgType(CommonEnrichProperty.getClass.getSimpleName.replace("$", ""))

  case class MsgType(str: String)
}
