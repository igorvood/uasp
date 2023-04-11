package ru.vtb.kafkatracer.request.meta.cache

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledSerrvice(val kafkaMessageListener:KafkaMessageListener) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduledSerrvice::class.java)

    @Scheduled(fixedDelay = 10000)
    fun asd(){
        logger.info("Readed ${kafkaMessageListener.total.get()} message. Find ${kafkaMessageListener.find.get()}.")
    }
}