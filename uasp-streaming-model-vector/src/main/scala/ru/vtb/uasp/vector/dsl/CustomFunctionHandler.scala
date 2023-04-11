package ru.vtb.uasp.vector.dsl

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.util.DateUtil
import ru.vtb.uasp.vector.util.ProductNmUtil.returnProductNm

object CustomFunctionHandler {
  val customFunctions: Map[String, (Any, UaspDto) => Any] = Map() ++
    Map("None" -> ((value: Any, uasp: UaspDto) => value)) ++
    Map("toTimeFormat(YYYY-MM-DD hh:mm:ss.SSS)" -> ((value: Any, uasp: UaspDto) => DateUtil.longToString(System.currentTimeMillis(), DateUtil.kafkaFormatter))) ++
    Map("toTimeFormat(YYYY-MM-DD hh:mm:ss)" -> ((value: Any, uasp: UaspDto) => DateUtil.longToString(value.toString.toLong, DateUtil.eventFormatter))) ++
    Map("toUpperCase" -> ((value: Any, uasp: UaspDto) => value.asInstanceOf[String].toUpperCase)) ++
    Map("returnProductNm" -> ((value: Any, uasp: UaspDto) =>  returnProductNm(value.asInstanceOf[String]))) ++
    Map("cardFormCalculate" -> ((value: Any, uasp: UaspDto) => if (value.asInstanceOf[String].equals("Y")) "Цифровая" else "Пластиковая" )) ++
    Map("validityCalculate" -> ((value: Any, uasp: UaspDto) => { value.asInstanceOf[String].substring(5, 7) + "/" + value.asInstanceOf[String].substring(2, 4)} )) ++
    Map("expireDt48Calculate" -> ((value: Any, uasp: UaspDto) => value.asInstanceOf[String].substring(2, 7) )) ++
    Map("salaryFlagCalculate" -> ((value: Any, uasp: UaspDto) => if (value.asInstanceOf[String].equals("Y")) "Y" else "N" )) ++
    Map("maskedFunction" -> ((value: Any, uasp: UaspDto) => {
      val sourceAcc = value.asInstanceOf[String]
      if (sourceAcc.isEmpty) sourceAcc else sourceAcc.substring(0, Math.min(10, sourceAcc.length)) + "******" + sourceAcc.substring(Math.min(16, sourceAcc.length))
    }))

}
