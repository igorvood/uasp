package ru.vtb.kafkatracer.configuration

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.AbstractMessageListenerContainer
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import ru.vtb.kafkatracer.appProps.TopicProp
import ru.vtb.kafkatracer.request.meta.cache.KafkaMessageListener
import java.util.UUID

@Configuration
class KafkaConfiguration {



    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, String> {
        val buildConsumerProperties = kafkaProperties.buildConsumerProperties()
        println(kafkaProperties.consumer.groupId)
        kafkaProperties.consumer.groupId=UUID.randomUUID().toString()
        println(kafkaProperties.consumer.groupId)
        return DefaultKafkaConsumerFactory(buildConsumerProperties)
    }

    @Bean
    fun kafkaListenerFactory1(
        topic: TopicProp,
        kafkaMessageListener: KafkaMessageListener,
        cnsFactory: ConsumerFactory<String, String>
    ): AbstractMessageListenerContainer<String, String> {
        val containerProperties = ContainerProperties(topic.name)
        containerProperties.messageListener = kafkaMessageListener
        val listenerContainer: ConcurrentMessageListenerContainer<String, String> =
            ConcurrentMessageListenerContainer(cnsFactory, containerProperties)
        listenerContainer.isAutoStartup = false
        // bean name is the prefix of kafka consumer thread name
        listenerContainer.setBeanName("kafka-message-listener-$topic")

        listenerContainer.start()

        return listenerContainer
    }
}