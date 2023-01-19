package ru.vtb.uasp.vector.config

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

import scala.collection.mutable


case class CasePropsModel(
                           appServiceName: ServiceDataDto,
                           //appSavepointPref: String,
                           appSyncParallelism: Int,
                           stateCheckpointsNumRetained: Int,
                           streamCheckpointTimeoutSeconds: Long,
                           //appStreamCheckpointTimeMilliseconds: Long,
                           caseProps: Map[String, Either[ReadConfigErrors, FlinkSinkProperties]],
                           consumer: FlinkConsumerProperties
                         ) {
  require(appSyncParallelism > 0, "appSyncParallelism must be grater than zero")

  val dlqProducer: FlinkSinkProperties = caseProps("dlq").right.get

}

object CasePropsModel extends PropertyCombiner[CasePropsModel] with ConfigurationInitialise[CasePropsModel] {
  val appPrefixDefaultName: String = "uasp-streaming-model-vector"

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, CasePropsModel] =
    for {
      appServiceName <- ServiceDataDto.create(s"$prf.service")
      //appSavepointPref <- propertyVal[String](s"$prf", "savepoint.pref")(appProps, configurationInitialise, s)
      appSyncParallelism <- propertyVal[Int](s"$prf", "mv.max.parallelism")
      stateCheckpointsNumRetained <- propertyVal[Int](s"$prf", "mv.state.checkpoints.num-retained")
      streamCheckpointTimeoutSeconds <- propertyVal[Long](s"$prf", "mv.stream.checkpoint.time.seconds")
      //appStreamCheckpointTimeMilliseconds <- propertyVal[Long](s"$prf", "appStreamCheckpointTimeMilliseconds")
      caseProps <- mapProperty(s"$prf.producer", (a, b, c) => createByClass(a, FlinkSinkProperties.getClass, s => FlinkSinkProperties.create(s)))
      fromTopic <- FlinkConsumerProperties.create(s"$prf.consumer")
    } yield CasePropsModel(
      appServiceName,
      //appSavepointPref,
      appSyncParallelism,
      stateCheckpointsNumRetained,
      streamCheckpointTimeoutSeconds,
      //appStreamCheckpointTimeMilliseconds,
      caseProps,
      fromTopic
  )


  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): CasePropsModel =
    CasePropsModel(prf)(allProps, CasePropsModel)
}
