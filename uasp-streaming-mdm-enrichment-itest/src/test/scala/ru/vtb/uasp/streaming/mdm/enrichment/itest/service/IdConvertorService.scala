package ru.vtb.uasp.streaming.mdm.enrichment.itest.service

import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.prefix

import java.util.Base64

object IdConvertorService {

  def localToGlobal(localId: String): String = {
    prefix + new String(Base64.getEncoder.encode(localId.replaceAll(prefix, "").getBytes()))
  }

  def globalToLocal(globalId: String): String = {
    prefix + new String(Base64.getDecoder.decode(globalId.replaceAll(prefix, "").getBytes()))
  }
}