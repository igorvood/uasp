package ru.vtb.uasp.inputconvertor.factory

import org.json4s.JValue
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao._
import ru.vtb.uasp.inputconvertor.dao.ca._
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel


trait UaspDtoParser {
  def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto
}

private class Way4UaspDtoParser() extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    Way4UaspDtoDao.fromJValue(mes, dtoMap)
}

private class MDMUaspDtoParser(uaspDtoType: String) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    MDMUaspDtoDao.fromJValue(mes, dtoMap, uaspDtoType)
}

private class ClientUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingClientUaspDtoDao.fromJValue(mes, dtoMap)
}

private class AccountBalanceUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingAccountBalanceUaspDtoDao.fromJValue(mes, dtoMap)
}

private class AccountUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingAccountUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CardUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingCardUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CurrencyUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CurrencyUaspDtoDao.fromJValue(mes, dtoMap)
}

private class FirstSalaryUaspDtoParser(propsModel: NewInputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    FirstSalaryUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class CAFirstSalaryUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CAFirstSalaryUaspDtoDao.fromJValue(mes,  dtoMap)
}

private class ProfileUaspDtoParser(propsModel: NewInputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    ProfileUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class ProfileAuthUaspDtoDaoParser(propsModel: NewInputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    ProfileAuthUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class WithDrawUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    WithdrawUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CardFlUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CaCardFlUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CACardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CaDepositFlUaspDtoParser extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CADepositFlUaspDtoDao.fromJValue(mes, dtoMap)
}


private class CustomerPackageUaspDtoStandart extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CustomerPackageUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CustomerProfileFullUaspDtoStandart extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CustomerIdProfileFullUaspDtoDao.fromJValue(mes, dtoMap)
}

private class UddsUaspDtoParser(propsModel: NewInputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    UddsUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class LoyaltyUaspDtoParser(propsModel: NewInputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    LoyaltyUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

object UaspDtoParserFactory {
  def apply(uaspDtoType: String, propsModel: NewInputPropsModel) = uaspDtoType.toUpperCase match {
    case "WAY4" => new Way4UaspDtoParser
    case "MDM" => new MDMUaspDtoParser(uaspDtoType)
    case "MDM-PROFILE" => new MDMUaspDtoParser(uaspDtoType)
    case "ISSIUNG-CLIENT" => new ClientUaspDtoParser
    case "ISSUING-ACCOUNT-BALANCE" => new AccountBalanceUaspDtoParser
    case "ISSUING-ACCOUNT" => new AccountUaspDtoParser
    case "ISSUING-CARD" => new CardUaspDtoParser
    case "CURRENCY" => new CurrencyUaspDtoParser
    case "FIRST-SALARY" => new FirstSalaryUaspDtoParser(propsModel)
    case "CA-FIRST-SALARY" => new CAFirstSalaryUaspDtoParser
    case "POS-TRANSACTION" => new Way4UaspDtoParser
    case "WAY4-FIRST-SALARY" => new Way4UaspDtoParser
    case "PROFILE" => new ProfileUaspDtoParser(propsModel)
    case "PROFILE-AUTH" => new ProfileAuthUaspDtoDaoParser(propsModel)
    case "WITHDRAW" => new WithDrawUaspDtoParser
    case "WAY4-WITHDRAW" => new Way4UaspDtoParser
    case "CARDFL" => new CardFlUaspDtoParser
    case "CA-CARDFL" => new CaCardFlUaspDtoParser
    case "CA-DEPOSITFL" => new CaDepositFlUaspDtoParser
    case "CUSTOMER-PACKAGE" => new CustomerPackageUaspDtoStandart
    case "CUSTOMER-PROFILE-FULL" => new CustomerProfileFullUaspDtoStandart
    case "UDDS" => new UddsUaspDtoParser(propsModel)
    case "LOYALTY" => new LoyaltyUaspDtoParser(propsModel)
    case _ => throw new RuntimeException("Wrong UaspDto type: " + uaspDtoType)

  }
}
