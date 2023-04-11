package ru.vtb.uasp.streaming.mdm.enrichment.itest.entity

case class CheckData(checkStatus: Boolean, checkMsg: String, startTime: Long = System.currentTimeMillis(), stopTime: Long = System.currentTimeMillis())
