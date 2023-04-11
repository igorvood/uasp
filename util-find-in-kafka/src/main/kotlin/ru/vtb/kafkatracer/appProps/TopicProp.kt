package ru.vtb.kafkatracer.appProps

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "topic")
data class TopicProp(
    val name: String,
    val findStr: String,
)
