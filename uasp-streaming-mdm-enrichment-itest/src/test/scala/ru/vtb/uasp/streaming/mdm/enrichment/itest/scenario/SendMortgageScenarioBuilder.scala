package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import com.github.mnogu.gatling.kafka.Predef._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroSerializeUtil
import ru.vtb.uasp.mdm.enrichment.service.dto.EnrichmentUaspWithError
import ru.vtb.uasp.streaming.mdm.enrichment.itest.action.{CheckAction, CheckerOfMortgage}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.Finisheable
import ru.vtb.uasp.streaming.mdm.enrichment.itest.dao.DataGeneratorDao
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.factory.ConsumerServiceFactory
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.CommonObject._
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendMortgageScenarioBuilder._
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.TestEnrichProperty.hypothecProperty
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.{ConsumerService, IdConvertorService}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.userIdLocal
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.KafkaPropertiesUtil

class SendMortgageScenarioBuilder( val countUsers: Int,
                                  val config: Config) extends Finisheable {
  private val consumerMortgageDto: ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] =
    ConsumerServiceFactory.getMdmMortgageConsumerService(config,
      KafkaPropertiesUtil.getConsumerKafkaProperties(config, config.groupIdMortgageMdm))
  private val logger = LoggerFactory.getLogger(getClass)

  def isFinished: Boolean = consumerMortgageDto.isFinished

  def getSendMortgageScenario: ScenarioBuilder = {

    val sendMdmMortgageScenario =
      scenario("Mortgage Links")
        .exec(session => {
          val localUserId = userIdLocal
          val updateSession = session
            .set(globalUserIdSessionName, IdConvertorService.localToGlobal(localUserId))
          updateSession
        })

        .exec(session => {
          val globalUserId = session(globalUserIdSessionName).as[String]
          val localMortgageUaspDto = DataGeneratorDao.generateMortgageMdm(globalUserId)
          val bytesMortgageUaspDto = AvroSerializeUtil.encode[UaspDto](localMortgageUaspDto, encoderUaspDto, genericDatumWriterUaspDto)
          val out = EnrichmentUaspWithError(globalUserId, localMortgageUaspDto, "", List.empty)
            .enrichValue((globalUserId.map(a => a.toInt).sum % 2 == 0).toString, hypothecProperty)

          session.set(localMortgageUaspDtoSessionName, out.uaspDto)
            .set(bytesGlobalUserIdSessionName, globalUserId.getBytes())
            .set(bytesMortgageUaspDtoSessionName, bytesMortgageUaspDto)
        })
        .exec(kafka("kafkaInMortgageMessages").send[Array[Byte], Array[Byte]]("${" + bytesGlobalUserIdSessionName + "}", "${" + bytesMortgageUaspDtoSessionName + "}"))
        .asLongAs(_ => consumerMortgageDto.getCountMessages < countUsers, "CheckCountMortgageStatusMessages") {
          exec(session => session).pause(1)
        }
        .exec(session => {
          val globalUserId = session(globalUserIdSessionName).as[String]
          val clusterMortgageUaspDto: UaspDto = consumerMortgageDto.get(globalUserId)
          session.set(clusterMortgageUaspDtoSessionName, clusterMortgageUaspDto)
        })
        .exec(new CheckAction("CheckerOfMortgageMdm", new CheckerOfMortgage()))
        .exec(session => {
          consumerMortgageDto.finish()
          session
        })

    sendMdmMortgageScenario
  }

}

object SendMortgageScenarioBuilder {

  val globalUserIdSessionName = "globalUserId"
  val countUsersSessionName = "countUsers"
  val localMortgageUaspDtoSessionName = "localMortgageUaspDto"
  val bytesGlobalUserIdSessionName = "bytesGlobalUserId"
  val bytesMortgageUaspDtoSessionName = "bytesMortgageUaspDto"
  val clusterMortgageUaspDtoSessionName = "clusterMortgageUaspDto"

}
