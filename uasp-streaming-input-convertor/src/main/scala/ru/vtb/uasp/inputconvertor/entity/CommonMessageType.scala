package ru.vtb.uasp.inputconvertor.entity

import org.json4s._

import java.time.{LocalDateTime, ZoneId}

case class CommonMessageType(
                              message_key: String,
                              message: Array[Byte],
                              message_str: Option[String] = None,
                              json_message: Option[JValue] = None,
                              json_schemakey: Option[String] = None,
                              json_schema: Option[String] = None,
                              avro_message: Option[Array[Byte]] = None,
                              error: Option[String] = None,
                              valid: Boolean = false,
                              check_timestamp: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
                            )
