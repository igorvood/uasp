package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import com.github.mnogu.gatling.kafka.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroSerializeUtil
import ru.vtb.uasp.mdm.enrichment.dao.UaspDtoPredef
import ru.vtb.uasp.mdm.enrichment.service.dto.EnrichmentUaspWithError
import ru.vtb.uasp.streaming.mdm.enrichment.itest.action.{CheckAction, CheckerOfEnrichmentWay4}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.Finisheable
import ru.vtb.uasp.streaming.mdm.enrichment.itest.dao.DataGeneratorDao
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.factory.ConsumerServiceFactory
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.CommonObject.{encoderUaspDto, genericDatumWriterUaspDto}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendWay4ScenarioBuilder._
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.TestEnrichProperty.{globalIdEnrichPropertyTest, hypothecProperty}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.{ConsumerService, IdConvertorService}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.userIdLocal
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.KafkaPropertiesUtil

class SendWay4ScenarioBuilder(val countUsers: Int,
                              val countTransactions: Int, val config: Config, val mdmCrossLinks: Finisheable,
                              val mortgage: Finisheable,
                              val rate: Finisheable,
                             ) {

  val countMessages: Int = countUsers * countTransactions
  val consumerEnrichmentDto: ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Map[String, UaspDto]] = ConsumerServiceFactory.getEnrichmentConsumerService(config,
    KafkaPropertiesUtil.getConsumerKafkaProperties(config, config.groupIdWay4))
  private val logger = LoggerFactory.getLogger(getClass)

  def getSendWay4Scenario: ScenarioBuilder = {
    val sendWay4Scenario = scenario("Way4")
      .exec(session => {
        val localUserId = userIdLocal
        val globalUserId = IdConvertorService.localToGlobal(localUserId)

        session
          .set(localUserIdSessionName, localUserId)
          .set(globalUserIdSessionName, globalUserId)
      })
      .asLongAs(_ => mdmCrossLinks.isFinished == false, "CheckIsFinishedMdmStatusMessages") {
        exec(session => session).pause(10)
      }
      .asLongAs(_ => mortgage.isFinished == false, "CheckIsFinishedMortgageStatusMessages") {
        exec(session => session).pause(10)
      }
      .asLongAs(_ => rate.isFinished == false, "CheckIsFinishedRateStatusMessages") {
        exec(session => session).pause(10)
      }
      .repeat(countTransactions)({
        exec(session => {
          val localUserId = session(localUserIdSessionName).as[String]
          val globalUserId = session(globalUserIdSessionName).as[String]
          val idMsg = userIdLocal

          val localWay4UaspDto = DataGeneratorDao.generateWay4(localUserId, idMsg)

          val localEnrichmentWay4UaspDto = UaspDtoPredef.PreDef(localWay4UaspDto)
            .enrichGlobalId(globalUserId, globalIdEnrichPropertyTest)

          val out = EnrichmentUaspWithError(globalUserId, localEnrichmentWay4UaspDto, "way4", List.empty)
            .enrichMainStream(List("hypothec" -> hypothecProperty)) {
              case "hypothec" => (globalUserId.map(a => a.toInt).sum % 2 == 0).toString
              case _ => throw new Exception("Отсутствует alias потока данных")
            }

          val bytesLocalUserId = localUserId.getBytes()
          val bytesWay4UaspDto = AvroSerializeUtil.encode[UaspDto](localWay4UaspDto, encoderUaspDto, genericDatumWriterUaspDto)

          session
            .set(bytesLocalUserIdSessionName, bytesLocalUserId)
            .set(bytesWay4UaspDtoSessionName, bytesWay4UaspDto)
        })
          .exec(kafka("kafkaInWay4MessagesConf").send[Array[Byte], Array[Byte]]("${" + bytesLocalUserIdSessionName + "}", "${" + bytesWay4UaspDtoSessionName + "}"))
      })
      .asLongAs(_ => consumerEnrichmentDto.getCountMessages < countMessages, "CheckCountWay4Messages") {
        exec(session => session).pause(10)
      }
      .exec(session => {
        new CheckAction("Compare_result&expectedResult_CheckerOfEnrichmentWay4", new CheckerOfEnrichmentWay4(consumerEnrichmentDto))
        session
      })
      .exec(session => {
        consumerEnrichmentDto.finish()
        session
      })

    sendWay4Scenario
  }
}

object SendWay4ScenarioBuilder {

  val localUserIdSessionName = "localUserId"
  val globalUserIdSessionName = "globalUserId"
  val localEnrichmentWay4UaspDtoMapSessionName = "localEnrichmentWay4UaspDtoMap"
  val bytesLocalUserIdSessionName = "bytesLocalUserId"
  val bytesWay4UaspDtoSessionName = "bytesWay4UaspDto"
  val clusterEnrichmentWay4UaspDtoMapSessionName = "clusterEnrichmentWay4UaspDtoMap"
  val rateFinishedSessionName = "rateFinishedSessionName"
}