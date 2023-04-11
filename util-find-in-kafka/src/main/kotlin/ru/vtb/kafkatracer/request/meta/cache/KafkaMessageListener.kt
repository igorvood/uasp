package ru.vtb.kafkatracer.request.meta.cache

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.MessageListener
import org.springframework.stereotype.Service
import ru.vtb.kafkatracer.appProps.TopicProp
import ru.vtb.kafkatracer.request.meta.cache.dto.KafkaData
import java.util.concurrent.atomic.AtomicLong


@Service
class KafkaMessageListener(val topic: TopicProp,) : MessageListener<String, String> {

    val total = AtomicLong(0)
    val find = AtomicLong(0)
    private val logger: Logger = LoggerFactory.getLogger(KafkaMessageListener::class.java)


    override fun onMessage(data: ConsumerRecord<String, String>) {
        val key = data.key()
        val headers = data.headers().toArray()
        val timestamp = data.timestamp()
        val value: String = data.value()
        val pip = data.topic()
        total.addAndGet(1)

       if(value.contains(topic.findStr)){
            logger.info("kafka key $key, value $value")
           find.addAndGet(1)
        }

//        KafkaData(key, headers, timestamp, value, pip)



    }
}