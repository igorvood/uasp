package ru.vtb.uasp.mutator

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.json.JsonUtil.getFieldsForCases
import ru.vtb.uasp.mutator.way4.Way4Cases2_7Test.testcasePostfix

import java.util.UUID
import scala.util.Random

object ValidUaspDtoGenerator {

  def createValidUaspDto(customerId: String, testcasePostfix: String): UaspDto = {

    def caseType = s"way4-case-$testcasePostfix"

    var mapTmpInt: Map[String, Int] = Map()
    var mapTmpLong: Map[String, Long] = Map()
    var mapTmpFloat: Map[String, Float] = Map()
    var mapTmpDouble: Map[String, Double] = Map()
    var mapTmpBoolean: Map[String, Boolean] = Map()
    var mapTmpString: Map[String, String] = Map()
    var mapTmpDecimal: Map[String, BigDecimal] = Map()

    val uuid: String = UUID.randomUUID().toString
    val process_time: Long = System.currentTimeMillis()


    if ("way4-case-2-2".equals(caseType)) {

      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      mapTmpString = mapTmpString + ("audit_auth_code" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_rrn" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_srn" -> UUID.randomUUID().toString)
      mapTmpDecimal = mapTmpDecimal + ("transaction_amount" -> BigDecimal.valueOf(11.11))
      mapTmpLong = mapTmpLong + ("transaction_datetime" -> (System.currentTimeMillis() - 10000l))
      mapTmpLong = mapTmpLong + ("effective_date" -> (System.currentTimeMillis() - 100l))
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")


      //необзятельные поля
      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)


    }

    else if ("way4-case-2-3".equals(caseType)) {

      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_srn" -> UUID.randomUUID().toString)
      mapTmpLong = mapTmpLong + ("transaction_datetime" -> System.currentTimeMillis())
      mapTmpLong = mapTmpLong + ("effective_date" -> System.currentTimeMillis())


      mapTmpString = mapTmpString + ("action_type" -> "AuthorizationReversal")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")

      //or
      //            mapTmpString = mapTmpString + ("action_type" -> "PresentmentReversal")
      //            mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")

      //or

      //      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      //      mapTmpString = mapTmpString + ("processing_resolution" -> "Rejected")


      mapTmpString = mapTmpString + ("audit_auth_code" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_rrn" -> UUID.randomUUID().toString)

    }
    else if ("way4-case-2-4".equals(caseType)) {

      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpLong = mapTmpLong + ("effective_date" -> System.currentTimeMillis())

      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      mapTmpString = mapTmpString + ("audit_auth_code" -> "")
      mapTmpString = mapTmpString + ("audit_rrn" -> "")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")

    }

    else if ("way4-case-2-7".equals(caseType)) {

      mapTmpString += ("local_id" -> UUID.randomUUID().toString)
//      mapTmpString += ("test" -> UUID.randomUUID().toString)
      mapTmpString += ("action_type" -> "Presentment")
      mapTmpString += ("service_type" -> "F7")
      mapTmpLong += ("service_datetime" -> System.currentTimeMillis())
      mapTmpLong += ("effective_date" -> System.currentTimeMillis())
      mapTmpString += ("processing_resolution" -> "Accepted")
      mapTmpDecimal += ("fee_amount" -> BigDecimal.valueOf(1.0))

    }

    else if ("way4-case-2-9".equals(caseType)) {

      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpLong = mapTmpLong + ("effective_date" -> System.currentTimeMillis())

      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      mapTmpString = mapTmpString + ("audit_auth_code" -> "")
      mapTmpString = mapTmpString + ("audit_rrn" -> "")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")

    }

    else if ("way4-case-2-10".equals(caseType)) {

      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpDecimal = mapTmpDecimal + ("transaction_amount" -> BigDecimal.valueOf(11.11))
      mapTmpLong = mapTmpLong + ("effective_date" -> System.currentTimeMillis())
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("action_type" -> "Authorization")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")
      mapTmpString = mapTmpString + ("processing_result_code" -> "0")


    } else if ("way4-case-5-2".equals(caseType)) {

      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_auth_code" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_rrn" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_srn" -> UUID.randomUUID().toString)
      mapTmpDecimal = mapTmpDecimal + ("transaction_amount" -> BigDecimal.valueOf(11.11))
      mapTmpLong = mapTmpLong + ("transaction_datetime" -> System.currentTimeMillis())

      mapTmpString = mapTmpString + ("action_type" -> "Authorization")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")
      mapTmpString = mapTmpString + ("service_type" -> "I5")
      //or


      mapTmpString = mapTmpString + ("PointOfService_mcc" -> "6666")
      mapTmpString = mapTmpString + ("PointOfService_terminalType" -> "test")


    } else if ("way4-case-5-3".equals(caseType)) {

      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)

      mapTmpString = mapTmpString + ("audit_auth_code" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_rrn" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("audit_srn" -> UUID.randomUUID().toString)
      mapTmpDecimal = mapTmpDecimal + ("transaction_amount" -> BigDecimal.valueOf(11.11))
      mapTmpLong = mapTmpLong + ("transaction_datetime" -> System.currentTimeMillis())
      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Debit")
      mapTmpString = mapTmpString + ("PointOfService_terminalType" -> "test")


    }
    else if ("way4-case-11-2".equals(caseType)) {

      mapTmpString = mapTmpString + ("operation_id" -> UUID.randomUUID().toString)
      mapTmpString = mapTmpString + ("local_id" -> UUID.randomUUID().toString)
      mapTmpLong = mapTmpLong + ("transaction_datetime" -> System.currentTimeMillis())

      mapTmpString = mapTmpString + ("action_type" -> "Presentment")
      mapTmpString = mapTmpString + ("processing_resolution" -> "Accepted")
      mapTmpString = mapTmpString + ("payment_direction" -> "Credit")
      mapTmpBoolean = mapTmpBoolean + ("isHypotec" -> true)


    }


    UaspDto(
      customerId,
      mapTmpInt,
      mapTmpLong,
      mapTmpFloat,
      mapTmpDouble,
      mapTmpDecimal,
      mapTmpString,
      mapTmpBoolean,
      uuid,
      process_time
    )
  }

}
