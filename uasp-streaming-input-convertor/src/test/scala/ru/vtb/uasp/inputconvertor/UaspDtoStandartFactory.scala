package ru.vtb.uasp.inputconvertor

import ru.vtb.uasp.common.dto.UaspDto


trait UaspDtostandard {
  def getstandardUaspDto(uuid: String): UaspDto
}

private class Way4UaspDtostandard() extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "2157291480",
    dataInt = Map[String, Int](),
    dataLong = Map("transaction_datetime" -> 1626631944000L) ++
      Map("processing_datetime" -> 1626631944000L) ++
      Map("effective_date" -> 1626566400000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("transaction_amount" -> BigDecimal(6651.78)),
    dataString = Map("operation_id" -> "493900") ++
      Map("local_id" -> "2157291480") ++
      Map("audit_auth_code" -> "3687") ++
      Map("processing_resolution" -> "Accepted") ++
      Map("audit_rrn" -> "4hvECqylkFgW") ++
      Map("action_type" -> "Authorization") ++
      Map("payment_direction" -> "Debit") ++
      Map("service_type" -> "I5") ++
      Map("mcc" -> "ABCDEFGHIJKLMNOPQRSTUVWXYZABC") ++
      Map("terminal_type" -> "IMPRINTER") ++
      Map("processing_result_code" -> "0") ++
      Map("card_expire_w4" -> "2405") ++
      Map("payment_scheme_w4" -> "VISA"),
    dataBoolean = Map[String, Boolean](),
    uuid =uuid,
    process_timestamp = 0
  )
}

private class MDMUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "324",
    dataInt = Map[String, Int](),
    dataLong = Map[String, Long](),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map[String, BigDecimal](),
    dataString = Map("local_id" -> "324") ++
      Map("global_id" -> "10324") ++
      Map("system_number" -> "OW4"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class IssiungClientUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "1457163490",
    dataInt = Map[String, Int](),
    dataLong = Map[String, Long](),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map[String, BigDecimal](),
    dataString = Map("local_id" -> "1457163490"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class IssiungCardUaspDtoStandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "111111750",
    dataInt = Map[String, Int](),
    dataLong = Map[String, Long](),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map[String, BigDecimal](),
    dataString = Map("local_id" -> "1111725110"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class IssuingAccountUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "560896180",
    dataInt = Map[String, Int](),
    dataLong = Map[String, Long](),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map[String, BigDecimal](),
    dataString = Map("local_id" -> "560896180"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class IssuingAccountBalanceUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "111111750",
    dataInt = Map[String, Int](),
    dataLong = Map[String, Long](),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map[String, BigDecimal](),
    dataString = Map("local_id" -> "111111750"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class CurrencyUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "78a3b0e4-4221-3020-8bad-080de84de4c6",
    dataInt = Map("rates_scale" -> 1),
    dataLong = Map("currency_date" -> 1653350400000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("rates_price" -> 41.398),
    dataString = Map("local_id" -> "78a3b0e4-4221-3020-8bad-080de84de4c6") ++
      Map("rates_currency_name" -> "Австралийский доллар") ++
      Map("rates_currency_numericCode" -> "036") ++
      Map("rates_currency_alphaCode" -> "AUD"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
  // rates_currency_name -> Австралийский доллар, rates_currency_numericCode -> 036, rates_currency_alphaCode -> AUD),Map(),,0)
}

//UaspDto(CFT2RS.10000033307567,Map(),Map(CFT2RS_CD_eventTime -> 1652877138000),
// Map(),Map(),Map(CFT2RS_CD_data_operationAmount_sum -> 500),
// Map(CFT2RS_CD_clientId -> 1538541282, CFT2RS_CD_data_account -> 42304840********0037,
// CFT2RS_CD_systemId -> CFT2RS, CFT2RS_CD_data_operationAmount_currency -> USD, local_id -> 1538541282, CFT2RS_CD_data_KBO -> 731820-01, CFT2RS_CD_vidDepType -> VKL),
// Map(),,0)
private class FirstSalaryUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "1538541282",
    dataInt = Map(),
    dataLong = Map("event_dttm_cft" -> 1652866338000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("amount_sum_cft" -> BigDecimal(500.00)) ++
      Map("transaction_amount_sum" -> BigDecimal(500.00)) ++
      Map("fee_amount_sum" -> BigDecimal(0)),
    dataString = Map("operation_id_cft" -> "CFT2RS.10000033307567") ++
      Map("mdm_id_cft" -> "1538541282") ++
      Map("source_account_cft" -> "42304840********0037") ++
      Map("amount_currency_cft" -> "USD") ++
      Map("source_system_cft" -> "CFT2RS") ++
      Map("kbo_cft" -> "731820-01") ++
      Map("data_transactionDate" -> "2022-05-18T12:31:22.4508222") ++
      Map("account_type_cft" -> "VKL") ++
      Map("account_name_cft" -> "Вклад в будущее Прайм") ++
      Map("account_dep_cft" -> "primebodbo") ++
      Map("data_status" -> "Processed") ++
      Map("transactionId" -> "TS.0000000001240066") ++
      Map("fee_amount_currency" -> "") ++
      Map("transaction_amount_currency" -> "USD") ++
      Map("hash_card_number" -> "94ee059335e587e501cc4bf90613e0814f00a7b08bc7c648fd865a2af6a22cc2"),
    dataBoolean = Map("first_add_cft" -> false) ++
      Map("data_debet" -> true),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class CaFirstSalaryUaspDtostandard extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "123",
    dataInt = Map(),
    dataLong = Map("event_dttm_ca" -> 1664093471000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("trans_amount_ca" -> 10.10),
    dataString = Map("mdm_id_ca" -> "123") ++
      Map("kbo_ca" -> "qwerty") ++
      Map("source_system_ca" -> "CA"),
    dataBoolean = Map(),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class ProfileUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "401037430091",
    dataInt = Map(),
    dataLong = Map("event_dttm_prf" -> 1658737654000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("transaction_amount_prf" -> BigDecimal(14031.04)) ++
      Map("endbal_prf" -> BigDecimal(41496.57)),
    dataString = Map("drcr_prf" -> "DR") ++
      Map("source_system_prf" -> "Profile") ++
      Map("ztsoatmc" -> "") ++
      Map("hash_card_number" -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855") ++
      Map("account_type_prf" -> "MS") ++
      Map("currency_code_prf" -> "RUB") ++
      Map("kbo_prf" -> "731800-22") ++
      Map("ztsoatmc" -> "") ++
      Map("hash_card_number" -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855") ++
      Map("tcmt" -> "tcmtTest 11111111111111111111") ++
      Map("tcmt_account_num" -> "********************") ++
      Map("account_number_prf" -> "401037430091") ++
      Map("spr_prf" -> "TS.0000002275627293") ++
      Map("tso" -> "FCID#40817810414154018746~MRPC29#DW^14031.04~RCID#88888~ZAUTHMET#CIFID~ZCCODE#0~ZCHNL#BACKOFFICE~ZMRPC1030#~ZPKGID#MULTICARTA~ZROLE#1~ZTFEEFLG#1~ZTRNTYPE#731800-22~ZLINKTYP#ENTDEP~ZLINKID#88888~ZLINKSYS#") ++
      Map("data_transactionDate" -> "2022-07-25 112734"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class ProfileAuthUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "661500000000001509142164448582426848501A",
    dataInt = Map("transaction_currency_cd" -> 643) ++
      Map("typ" -> 1),
    dataLong = Map("local_transaction_dttm" -> 1644457024000L, "transmission_dttm" -> 1644485824000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("transaction_amt" -> 1),
    dataString = Map("source_system_prf_auth" -> "Profile-auth") ++
      Map("msgid" -> "661500000000001509142164448582426848501A") ++
      Map("card_number" -> "536829*****5948") ++
      Map("card_number_sha_256" -> "639499b05babb36c8c96420068febbd52ad74f82196c0a0b2ece38f026a0e101") ++
      Map("card_number_sha_256_md5" -> "f9e69f301d24041e790eec74b3851c1fdcc0fc58503092e099c174a4a9d918b3") ++
      Map("terminal_owner" -> "DEVICE IN MOSCOW") ++
      Map("message_type" -> "0100") ++
      Map("transaction_cd" -> "132") ++
      Map("response_code" -> "00001") ++
      Map("terminal_id" -> "1120") ++
      Map("mcc" -> "6011") ++
      Map("currency_alpha_code" -> "RUR") ++
      Map("terminal_class" -> "000") ++
      Map("date_time_string" -> "0210093704") ++
      Map("replacement_amt" -> "1145"),
    dataBoolean = Map[String, Boolean](),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class CardFlUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "990654099",

    dataInt = Map("RtSpnd30Cnt" -> 654456) ++
      Map("RtSpnd60Cnt" -> 882233) ++
      Map("CrtXzKkTpCd" -> 1) ++
      Map("RtSpnd90Cnt" -> 664455),

    dataLong = Map("BnsCtgChDt" -> 22443079241L) ++
      Map("PkgClsDt" -> 1926300395L) ++
      Map("PkgStrtDt" -> 1729600613L) ++
      Map("VtbOnlnLstSsnDt" -> 2364232300L) ++
      Map("ZFrntClntId" -> 123654L),

    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),

    dataDecimal = Map("BnsBlncAmt" -> BigDecimal.valueOf(666555444)) ++
      Map("CrdCrdt1MAmt" -> BigDecimal.valueOf(126.575)) ++
      Map("CrdCrdt2MAmt" -> BigDecimal.valueOf(127)) ++
      Map("CrdCrdt3MAmt" -> BigDecimal.valueOf(128)) ++
      Map("CrdDbt1MAmt" -> BigDecimal.valueOf(123)) ++
      Map("CrdDbt2MAmt" -> BigDecimal.valueOf(124)) ++
      Map("CrdDbt3MAmt" -> BigDecimal.valueOf(125)) ++
      Map("CrdPrch1MAmt" -> BigDecimal.valueOf(129)) ++
      Map("CrdPrch2MAmt" -> BigDecimal.valueOf(130.89)) ++
      Map("CrdPrch3MAmt" -> BigDecimal.valueOf(131.56)) ++
      Map("CrdTxn1MCnt" -> BigDecimal.valueOf(132.77)) ++
      Map("CrdTxn2MCnt" -> BigDecimal.valueOf(133.66)) ++
      Map("CrdTxn3MCnt" -> BigDecimal.valueOf(134.34)) ++
      Map("IncmCnfAmtNc" -> BigDecimal.valueOf(5353345)) ++
      Map("InvstPrtfAmtNc" -> BigDecimal.valueOf(765987)) ++
      Map("RtSpnd30Amt" -> BigDecimal.valueOf(112233)) ++
      Map("RtSpnd60Amt" -> BigDecimal.valueOf(991122)) ++
      Map("RtSpnd90Amt" -> BigDecimal.valueOf(773344.45)) ++
      Map("TtAnnAmtNc" -> BigDecimal.valueOf(458856)) ++
      Map("TtBlncAmt" -> BigDecimal.valueOf(123.45)),

    dataString = Map("mdmid" -> "990654099") ++
      Map("CrtXzFdTpCd" -> "SALARY") ++
      Map("PackDsc" -> "TestString") ++
      Map("system_source" -> "cardfl") ++
      Map("BnsCtgCd" -> "Код123") ++
      Map("CntrCd" -> "Russia") ++
      Map("GndrCd" -> "M") ++
      Map("RgnCd" -> "136") ++
      Map("ActCatMDMCd" -> "TestString") ++
      Map("SrvcMdl" -> "TestString") ++
      Map("PfmManSapId" -> "TestString") ++
      Map("IntOrgGenId" -> "TestString") ++
      Map("ZfrntChngCd" -> "TestString"),

    dataBoolean = Map("ChStLCFlg" -> true) ++
      Map("CtprtFlg" -> true) ++
      Map("IpFlg" -> true) ++
      Map("PrspFlg" -> true) ++
      Map("StLstCrmFlg" -> false) ++
      Map("VtbOnlnAcsFlg" -> false) ++
      Map("OpnFlg" -> true) ++
      Map("ZpBs144Flg" -> false),

    uuid = uuid,
    process_timestamp = 0
  )
}

private class CaCardFlUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "990654099",
    dataInt = Map(),
    dataLong = Map(),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("credit_limit_amt" -> BigDecimal.valueOf(10.01)),
    dataString = Map("hash_card_number" -> "990654099") ++
      Map("hash_sha256md5_card_number" -> "aaa") ++
      Map("mask_card_number" -> "123***213") ++
      Map("customer_id_and_masked_card_number" -> "customer_id_and_masked_card_number") ++
      Map("customer_id" -> "455") ++
      Map("mdm_id" -> "123") ++
      Map("source_system_cd" -> "M") ++
      Map("pos_flg" -> "pos_flg") ++
      Map("account_num" -> "222") ++
      Map("is_virtual_card_flg" -> "v") ++
      Map("card_expiration_dt" -> "2210") ++
      Map("payment_system_desc" -> "desc") ++
      Map("card_type_cd" -> "master") ++
      Map("salary_serv_pack_flg" -> "salary_serv_pack_flg") ++
      Map("salary_project_flg" -> "salary_project_flg") ++
      Map("salary_account_scheme_flg" -> "salary_account_scheme_flg") ++
      Map("salary_card_type_flg" -> "TestString") ++
      Map("contract_card_type_cd" -> "contract_card_type_cd") ++
      Map("loan_insurance_flg" -> "loan_insurance_flg"),

    dataBoolean = Map(),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class CaDepositFlUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "12345",
    dataInt = Map("period" -> 3),
    dataLong = Map(),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map("product_rate" -> BigDecimal.valueOf(10.01)),
    dataString = Map("account_num" -> "12345") ++
      Map("mdm_id" -> "123") ++
      Map("product_nm" -> "nm"),

    dataBoolean = Map(),
    uuid = uuid,
    process_timestamp = 0
  )
}

private class CardFlNullUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "990654099",

    dataInt = Map[String, Int](),

    dataLong = Map[String, Long](),

    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),

    dataDecimal = Map[String, BigDecimal](),

    dataString = Map("mdmid" -> "990654099") ++
      Map("system_source" -> "cardfl"),

    dataBoolean = Map[String, Boolean](),

    uuid = uuid,
    process_timestamp = 0
  )
}

private class UddsUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "1223134231",
    dataInt = Map(),
    dataLong = Map("eventDttm" -> 1643709240000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),

    dataDecimal = Map("operationAmount" -> BigDecimal.valueOf(1000.012)),

    dataString = Map("mdmId" -> "1223134231") ++
      Map("operationId" -> "348snd-32sd-wwd3-2kdjs3") ++
      Map("goalCurrency" -> "RUB") ++
      Map("tradingPlatform" -> "основной рынок") ++
      Map("balanceBeforeOperation" -> "SALARY") ++
      Map("interactionChannel" -> "SALARY") ++
      Map("balanceBeforeOperation" -> "1000") ++
      Map("interactionChannel" -> "SALARY") ++
      Map("operationType" -> "REFUND") ++
      Map("sourceAccount" -> "4081781***********8309") ++
      Map("sourceAccountBic" -> "BIC.044525745") ++
      Map("sourceAccountType" -> "CURRENT_ACCOUNT") ++
      Map("targetAccount" -> "4081781***********8309") ++
      Map("creditTransactionId" -> "TS.0000002263298966") ++
      Map("targetAccountBic" -> "BIC.044525745") ++
      Map("system_source" -> "udds") ++
      Map("targetAccountType" -> "BROKERAGE_ACCOUNT") ++
      Map("eventDttm" -> "2022-02-01 09:54:00") ++
      Map("hash_card_number" -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),

    dataBoolean = Map(),

    uuid = uuid,
    process_timestamp = 0
  )
}

private class LoyaltyUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "32542135",
    dataInt = Map(),
    dataLong = Map("eventDttm" -> 1666171516000L),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),

    dataDecimal = Map(),

    dataString = Map("mdmId" -> "32542135") ++
      Map("system_source" -> "loyalty") ++
      Map("hash_card_number" -> "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855") ++
      Map("loyaltyCode" -> "NoBonus") ++
      Map("eventType" -> "NEW_OPTION"),

    dataBoolean = Map(),

    uuid = uuid,
    process_timestamp = 0
  )
}

private class CustomerPackageUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "1223134231",
    dataInt = Map(),
    dataLong = Map(),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map(),

    dataString = Map("mdm_id" -> "1223134231") ++
      Map("package_nm" -> "456789") ++
      Map("multibonus_flg" -> "Y") ++
      Map("is_deleted" -> "0") ++
      Map("pension_flg" -> "N"),

    dataBoolean = Map(),

    uuid = uuid,
    process_timestamp = 0
  )
}

private class CustomerProfileFullUaspDtoStandart extends UaspDtostandard {
  override def getstandardUaspDto(uuid: String): UaspDto = UaspDto(id = "897",
    dataInt = Map(),
    dataLong = Map(),
    dataFloat = Map[String, Float](),
    dataDouble = Map[String, Double](),
    dataDecimal = Map(),

    dataString = Map("contract_id" -> "897") ++
      Map("customer_id" -> "3812") ++
      Map("contract_num" -> "194"),

    dataBoolean = Map(),

    uuid = uuid,
    process_timestamp = 0
  )
}


object UaspDtostandardFactory {
  def apply(uaspDtoType: String): UaspDtostandard = uaspDtoType.toUpperCase match {
    case "WAY4" => new Way4UaspDtostandard
    case "MDM" => new MDMUaspDtostandard
    case "ISSIUNG-CLIENT" => new IssiungClientUaspDtostandard
    case "ISSUING-CARD" => new IssiungCardUaspDtoStandard
    case "ISSUING-ACCOUNT" => new IssuingAccountUaspDtostandard
    case "ISSUING-ACCOUNT-BALANCE" => new IssuingAccountBalanceUaspDtostandard
    case "CURRENCY" => new CurrencyUaspDtostandard
    case "FIRST-SALARY" => new FirstSalaryUaspDtostandard
    case "CA-FIRST-SALARY" => new CaFirstSalaryUaspDtostandard
    case "PROFILE" => new ProfileUaspDtoStandart
    case "PROFILE-AUTH" => new ProfileAuthUaspDtoStandart
    case "CARDFL" => new CardFlUaspDtoStandart
    case "CA-CARDFL" => new CaCardFlUaspDtoStandart
    case "CA-DEPOSITFL" => new CaDepositFlUaspDtoStandart
    case "CARDFLNULL" => new CardFlNullUaspDtoStandart
    case "UDDS" => new UddsUaspDtoStandart
    case "LOYALTY" => new LoyaltyUaspDtoStandart
    case "CUSTOMER-PACKAGE" => new CustomerPackageUaspDtoStandart
    case "CUSTOMER-PROFILE-FULL" => new CustomerProfileFullUaspDtoStandart
    case _ => throw new RuntimeException("Wrong UaspDto type: " + uaspDtoType)

  }
}
