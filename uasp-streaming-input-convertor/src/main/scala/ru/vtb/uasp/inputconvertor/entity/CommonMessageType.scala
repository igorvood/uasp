package ru.vtb.uasp.inputconvertor.entity

import org.json4s._

import java.time.{LocalDateTime, ZoneId}

case class CommonMessageType(
                              message_key: String, // используется
                              message: Array[Byte],
                              message_str: Option[String] = None,
                              json_message: Option[JValue] = None,
                              json_schemakey: Option[String] = None,
                              json_schema: Option[String] = None,
                              avro_message: Option[Array[Byte]] = None, // используется
                              error: Option[String] = None,
                              valid: Boolean = false,

                            )
