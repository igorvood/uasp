package ru.vtb.uasp.pilot.model.vector.service

import org.apache.flink.api.common.functions.RichMapFunction
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.pilot.model.vector.constants.ModelVector._
import ru.vtb.uasp.pilot.model.vector.constants.ProductNm.returnProductNm
import ru.vtb.uasp.pilot.model.vector.dao.JsonUtil.getFieldsForCases
import ru.vtb.uasp.pilot.model.vector.dao.ModelVectorDao

import java.time.Instant


class AggregateMapService(case29: String = "") extends RichMapFunction[UaspDto, UaspDto] {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  private val modelVectorDao: ModelVectorDao = new ModelVectorDao

  val fieldsMap = getFieldsForCases(FULL_VECTOR)

  override def map(inMsg: UaspDto): UaspDto = {
    try {

      //TODO убрать отсюда
      //Маскирование счета
      val sourceAcc: String = inMsg.dataString.getOrElse("source_account_w4",
        inMsg.dataString.getOrElse("source_account_cft",
          inMsg.dataString.getOrElse("source_account",
            inMsg.dataString.getOrElse("contract_num",
              inMsg.dataString.getOrElse("account_num",
                inMsg.dataString.getOrElse("sourceAccount", "********************"))))))
      val sourceAccMasked = sourceAcc.substring(0, Math.min(10, sourceAcc.length)) + "******" + sourceAcc.substring(Math.min(16, sourceAcc.length))


      val depAcc = inMsg.dataString.getOrElse("DEP_ACCOUNT", "********************")
      val depAccMasked = depAcc.substring(0, Math.min(10, depAcc.length)) + "******" + depAcc.substring(Math.min(16, depAcc.length))

      val salaryFlg = if (inMsg.dataString.getOrElse("salary_serv_pack_flg", "").equals("Y") ||
        inMsg.dataString.getOrElse("salary_project_flg", "").equals("Y") ||
        inMsg.dataString.getOrElse("alary_account_scheme_flg", "").equals("Y") ||
        inMsg.dataString.getOrElse("salary_card_type_flg", "").equals("Y")) "Y" else "N"

      val card_expiration_dt = inMsg.dataString.getOrElse("card_expiration_dt", "XXXXXXXX")
      val expireDt48 = card_expiration_dt.substring(2, 7)

      val VALIDITY = card_expiration_dt.substring(5, 7) + "/" + card_expiration_dt.substring(2, 4)

      val PRODUCT_NM = returnProductNm(inMsg.dataString.getOrElse("contract_card_type_cd", ""))
      val CARD_FORM = if (inMsg.dataString.getOrElse("is_virtual_card_flg", "").equals("Y")) "Цифровая"
      else "Пластиковая"

      //29 кейс дублирует некоторые кейсы сделать нормально
      val caseName = if (case29.isEmpty) {
        val cases = inMsg.dataString.getOrElse(CLASSIFICATION, "").split(",")
        val currentCase = cases.filterNot(x => CASE_29.equals(x))
        if (!currentCase.isEmpty) {
          currentCase(0)
        }
        else case29
      } else case29
      val fields = fieldsMap(caseName)

      //TODO moscow time
      //TODO дату по заданному формату
      val result = modelVectorDao.map(inMsg.copy(
        dataString = inMsg.dataString + ("KAFKA_DTTM" ->
          Instant.ofEpochMilli(System.currentTimeMillis() + 3 * 60 * 60 * 1000).toString.replaceAll("T", " ").replaceAll("Z", ""))
          //          DateTime.now().plusHours(3).toString)
          + ("MDM_ID" -> inMsg.id)
          + ("source_acc_masked" -> sourceAccMasked)
          + ("dep_acc_masked" -> depAccMasked)
          + ("CARD_TYPE" -> inMsg.dataString.getOrElse("card_type_cd", "").toUpperCase)
          + ("SALARY_FLG" -> salaryFlg)
          + ("EXPIRE_CARD_DT48" -> expireDt48)
          + ("PRODUCT_NM" -> PRODUCT_NM)
          + ("CARD_FORM" -> CARD_FORM)
          + ("VALIDITY" -> VALIDITY)

      ),
        fields)
      println(result)
      result
    } catch {
      case e: Throwable => e match {
        case _ =>
          logger.error(e.getMessage)
          throw new RuntimeException(e)
      }
    }
  }

}
