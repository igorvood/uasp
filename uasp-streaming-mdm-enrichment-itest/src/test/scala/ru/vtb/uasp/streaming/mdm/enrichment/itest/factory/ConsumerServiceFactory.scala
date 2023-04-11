package ru.vtb.uasp.streaming.mdm.enrichment.itest.factory

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.streaming.mdm.enrichment.itest.dao.{DlqAccumulator, UaspDtoAccumulator, UaspDtoUserMsgMapAccumulator}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.ConsumerService

import java.util.Properties
import scala.collection.JavaConverters.asJavaCollectionConverter

object ConsumerServiceFactory {

  def getEnrichmentConsumerService(config: Config, consumerKafkaProperties: Properties):
  ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Map[String, UaspDto]] = {
    val accumulator: UaspDtoUserMsgMapAccumulator = new UaspDtoUserMsgMapAccumulator()
    val transformConsumerService = new ConsumerService(consumerKafkaProperties, Seq(config.topicOutEnrichmentWay4).asJavaCollection,
      accumulator)
    transformConsumerService.start()

    transformConsumerService
  }

  def getMdmStatusConsumerService(config: Config, consumerKafkaProperties: Properties):
  ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] = {
    val accumulator: UaspDtoAccumulator = new UaspDtoAccumulator()
    val transformConsumerService = new ConsumerService(consumerKafkaProperties,
      Seq(config.topicOutCrossLinkMdmStatus).asJavaCollection, accumulator)
    transformConsumerService.start()

    transformConsumerService
  }

  def getMdmMortgageConsumerService(config: Config, consumerKafkaProperties: Properties):
  ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] = {
    val accumulator: UaspDtoAccumulator = new UaspDtoAccumulator()
    val transformConsumerService = new ConsumerService(consumerKafkaProperties,
      Seq(config.topicOutMortgage).asJavaCollection, accumulator)
    transformConsumerService.start()

    transformConsumerService
  }

  def getEnrichmentDlqConsumerService(config: Config, consumerKafkaProperties: Properties):
  ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] = {
    val accumulator: DlqAccumulator = new DlqAccumulator()
    val transformConsumerService = new ConsumerService(consumerKafkaProperties, Seq(config.topicDLQ).asJavaCollection,
      accumulator)
    transformConsumerService.start()

    transformConsumerService
  }

}
