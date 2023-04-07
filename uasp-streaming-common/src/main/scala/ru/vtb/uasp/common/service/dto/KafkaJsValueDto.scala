package ru.vtb.uasp.common.service.dto

import play.api.libs.json.JsValue

case class KafkaJsValueDto(
                            id: String,
                            jsValue: JsValue,
                          )
