package ru.vtb.uasp.vector.util

import ru.vtb.uasp.common.dto.UaspDto

object UaspUtil {
  def getSystemName(inMsg: UaspDto): Option[String] = {
    if (inMsg.dataString.contains("source_system_w4") && inMsg.dataString("source_system_w4") == "WAY4") {
      Some("WAY4")
    } else if (inMsg.dataString.contains("source_system_prf") && inMsg.dataString("source_system_prf") == "Profile") {
      Some("PROFILE")
    } else if (inMsg.dataString.contains("source_system_prf_auth") && inMsg.dataString("source_system_prf_auth") == "Profile-auth") {
      Some("PROFILE")
    } else if (inMsg.dataString.contains("source_system_cft") && inMsg.dataString("source_system_cft") == "CFT2RS") {
      Some("CFT")
    } else if (inMsg.dataBoolean.contains("is_withdraw") && inMsg.dataBoolean("is_withdraw")) {
      Some("WITHDRAW")
    } else if (inMsg.dataString.contains("system_source")) {
      Some(inMsg.dataString("system_source").toUpperCase)
    }
    else {
      None
    }
  }
}
