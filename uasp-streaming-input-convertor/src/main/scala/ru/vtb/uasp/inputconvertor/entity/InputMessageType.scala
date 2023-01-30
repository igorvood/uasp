package ru.vtb.uasp.inputconvertor.entity

case class InputMessageType(
                             message_key: String,
                             message: Array[Byte],
                             headers: Map[String, String]
                           )
