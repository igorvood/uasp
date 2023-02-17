package ru.vtb.uasp.inputconvertor.service.dto

import ru.vtb.uasp.common.dto.UaspDto

case class UaspAndKafkaKey(kafkaKey: String,
                           uaspDto: UaspDto
                          )
