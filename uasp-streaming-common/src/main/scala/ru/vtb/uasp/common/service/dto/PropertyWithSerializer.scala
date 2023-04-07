package ru.vtb.uasp.common.service.dto

import ru.vtb.uasp.common.kafka.FlinkSinkProperties

case class PropertyWithSerializer[IN](
                                       flinkSinkProperties: FlinkSinkProperties,
                                       serializerToKafkaJsValue: IN => KafkaJsValueDto
                                     )
