package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import com.github.mnogu.gatling.kafka.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroSerializeUtil
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.FooCounter
import ru.vtb.uasp.streaming.mdm.enrichment.itest.dao.DataGeneratorDao
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.factory.ConsumerServiceFactory
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.CommonObject.{encoderUaspDto, genericDatumWriterUaspDto}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendOnlyWay4ScenarioBuilder._
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.ConsumerService
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.userIdLocal
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.KafkaPropertiesUtil

class SendOnlyWay4ScenarioBuilder( val countUsers: Int,
                                  val countTransactions: Int, val config: Config) {

  val msgId: FooCounter = new FooCounter(0)
  val countMessages: Int = countUsers * countTransactions
  val consumerEnrichmentDlq: ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] = ConsumerServiceFactory.getEnrichmentDlqConsumerService(config,
    KafkaPropertiesUtil.getConsumerKafkaProperties(config, config.groupIdDlq))
  private val logger = LoggerFactory.getLogger(getClass)

  def getSendOnlyWay4Scenario: ScenarioBuilder = {
    logger.info("Link 287269 , TmsLink 112")

    val sendOnlyWay4Scenario = scenario("OnlyWay4")
      .exec(session => {
        session
          .set(localUserIdSessionName, userIdLocal)
      })
      .repeat(countTransactions)({
        exec(session => {
          val localUserId = session(localUserIdSessionName).as[String]

          val localWay4UaspDto = DataGeneratorDao.generateWay4(localUserId, userIdLocal)

          val bytesLocalUserId = localUserId.getBytes()
          val bytesWay4UaspDto = AvroSerializeUtil.encode[UaspDto](localWay4UaspDto, encoderUaspDto, genericDatumWriterUaspDto)

          session
            .set(bytesLocalUserIdSessionName, bytesLocalUserId)
            .set(bytesWay4UaspDtoSessionName, bytesWay4UaspDto)
        })
          .exec(kafka("kafkaInWay4MessagesConf").send[Array[Byte], Array[Byte]]("${" + bytesLocalUserIdSessionName + "}", "${" + bytesWay4UaspDtoSessionName + "}"))
      })
      .asLongAs(_ => consumerEnrichmentDlq.getCountMessages < countMessages, "CheckCountWay4Messages") {
        exec(session => session).pause(10)
      }
      .exec(session => {
        consumerEnrichmentDlq.finish()
        session
      })

    sendOnlyWay4Scenario
  }
}


object SendOnlyWay4ScenarioBuilder {
  val localUserIdSessionName = "localUserId"
  val countMessagesSessionName = "countMessages"
  val bytesLocalUserIdSessionName = "bytesLocalUserId"
  val bytesWay4UaspDtoSessionName = "bytesWay4UaspDto"
}