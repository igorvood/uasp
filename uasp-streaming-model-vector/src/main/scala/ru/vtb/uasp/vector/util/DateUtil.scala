package ru.vtb.uasp.vector.util

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}


object DateUtil {
  val eventFormatter: org.joda.time.format.DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  val kafkaFormatter: org.joda.time.format.DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

  def longToString(datetime: Long, form: DateTimeFormatter, timezone: String = "Europe/Moscow"): String = {
    val dateTimeInstant = new DateTime(datetime, DateTimeZone.forID(timezone))

    form.print(dateTimeInstant)
  }
}
