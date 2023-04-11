package ru.vtb.kafkatracer.request.meta.cache.dto

import org.apache.kafka.common.header.Header

data class KafkaData(
    val key: String?,
    val headers: Array<Header>,
    val timestamp: Long,
    val value: String?,
    val topic: String?,
) {

}
