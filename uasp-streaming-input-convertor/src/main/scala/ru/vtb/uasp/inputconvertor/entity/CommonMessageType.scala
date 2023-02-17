package ru.vtb.uasp.inputconvertor.entity

import org.json4s._

import java.time.{LocalDateTime, ZoneId}

case class CommonMessageType(
                              message_key: String, // используется
                              json_message: Option[JValue] = None,
                              outBytes: Option[Array[Byte]] = None, // используется
                              error: Option[String] = None,
                              valid: Boolean = false,

                            )
