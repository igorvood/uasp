package ru.vtb.uasp.common.abstraction

class FlinkStreamConsumerPredef {

  //  implicit class NewStreamExecutionEnvironmentPredef(val self: StreamExecutionEnvironment) extends AnyVal {
  //
  //    def registerConsumerWithMetric1[O: TypeInformation](
  //                                                        serviceData: ServiceDataDto,
  //                                                        consumerProperties: FlinkConsumerProperties,
  //                                                        dlqProducer: Option[FlinkSinkProperties],
  //                                                        serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, JsMaskedPathError],
  //                                                        producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto]
  //                                                      ): DataStream[O] = {
  //      val consumer = consumerProperties.createConsumer()
  //
  //
  //      val mainStream = self.addSource(consumer)
  //        .process(serialisationProcessFunction)
  //
  //
  //      dlqProducer.map { dlq =>
  //        val errorStream = mainStream
  //          .getSideOutput(serialisationProcessFunction.dlqOutPut)
  //        errorStream.maskedProducerF(
  //          serviceData = serviceData,
  //          dlq,
  //
  //          { (a, s) => a.serializeToBytes(s) },
  //          sinkDlqProperty = Some(dlq, { (a, s) => {
  //
  //            val errs = a.data.map(m =>
  //              m.error :: a.errors)
  //              .getOrElse(a.errors)
  //            a.copy[JsMaskedPathError](errors = errs, data = None).serializeToBytes(s)
  //          }
  //          }),
  //          producerFactory = producerFactory
  //        )
  //
  //
  //      }
  //      mainStream
  //    }
  //  }

}
