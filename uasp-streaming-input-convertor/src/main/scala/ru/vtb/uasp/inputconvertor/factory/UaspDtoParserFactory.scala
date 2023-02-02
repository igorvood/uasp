package ru.vtb.uasp.inputconvertor.factory

import org.json4s.JValue
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao._
import ru.vtb.uasp.inputconvertor.dao.ca._
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.language.implicitConversions


trait UaspDtoParser {

  val propsModel: InputPropsModel

  def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto
}

private class Way4UaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    Way4UaspDtoDao.fromJValue(mes, dtoMap)
}

private class MDMUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    MDMUaspDtoDao.fromJValue(mes, dtoMap, propsModel.uaspdtoType)
}

private class ClientUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingClientUaspDtoDao.fromJValue(mes, dtoMap)
}

private class AccountBalanceUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingAccountBalanceUaspDtoDao.fromJValue(mes, dtoMap)
}

private class AccountUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingAccountUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CardUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    IssuingCardUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CurrencyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CurrencyUaspDtoDao.fromJValue(mes, dtoMap)
}

private class FirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    FirstSalaryUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class CAFirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CAFirstSalaryUaspDtoDao.fromJValue(mes, dtoMap)
}

private class ProfileUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser with Serializable {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    ProfileUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class ProfileAuthUaspDtoDaoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    ProfileAuthUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class WithDrawUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    WithdrawUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CaCardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CACardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CaDepositFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CADepositFlUaspDtoDao.fromJValue(mes, dtoMap)
}


private class CustomerPackageUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CustomerPackageUaspDtoDao.fromJValue(mes, dtoMap)
}

private class CustomerProfileFullUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    CustomerIdProfileFullUaspDtoDao.fromJValue(mes, dtoMap)
}

private class UddsUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    UddsUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

private class LoyaltyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JValue, dtoMap: Map[String, Array[String]]): UaspDto =
    LoyaltyUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

object UaspDtoParserFactory {
  def apply(propsModel: InputPropsModel) = propsModel.uaspdtoType.toUpperCase match {
    case "WAY4" => new Way4UaspDtoParser(propsModel)
    case "MDM" => new MDMUaspDtoParser(propsModel)
    case "MDM-PROFILE" => new MDMUaspDtoParser(propsModel)
    case "ISSIUNG-CLIENT" => new ClientUaspDtoParser(propsModel)
    case "ISSUING-ACCOUNT-BALANCE" => new AccountBalanceUaspDtoParser(propsModel)
    case "ISSUING-ACCOUNT" => new AccountUaspDtoParser(propsModel)
    case "ISSUING-CARD" => new CardUaspDtoParser(propsModel)
    case "CURRENCY" => new CurrencyUaspDtoParser(propsModel)
    case "FIRST-SALARY" => new FirstSalaryUaspDtoParser(propsModel)
    case "CA-FIRST-SALARY" => new CAFirstSalaryUaspDtoParser(propsModel)
    case "POS-TRANSACTION" => new Way4UaspDtoParser(propsModel)
    case "WAY4-FIRST-SALARY" => new Way4UaspDtoParser(propsModel)
    case "PROFILE" => new ProfileUaspDtoParser(propsModel)
    case "PROFILE-AUTH" => new ProfileAuthUaspDtoDaoParser(propsModel)
    case "WITHDRAW" => new WithDrawUaspDtoParser(propsModel)
    case "WAY4-WITHDRAW" => new Way4UaspDtoParser(propsModel)
    case "CARDFL" => new CardFlUaspDtoParser(propsModel)
    case "CA-CARDFL" => new CaCardFlUaspDtoParser(propsModel)
    case "CA-DEPOSITFL" => new CaDepositFlUaspDtoParser(propsModel)
    case "CUSTOMER-PACKAGE" => new CustomerPackageUaspDtoStandart(propsModel)
    case "CUSTOMER-PROFILE-FULL" => new CustomerProfileFullUaspDtoStandart(propsModel)
    case "UDDS" => new UddsUaspDtoParser(propsModel)
    case "LOYALTY" => new LoyaltyUaspDtoParser(propsModel)
    case _ => throw new RuntimeException("Wrong UaspDto type: " + propsModel.uaspdtoType)

  }
}

object UaspdtoTypeEnum extends Enumeration {
  protected case class Val(parser: InputPropsModel => UaspDtoParser) extends super.Val

  implicit def valueToPlanetVal(x: Value): Val = x.asInstanceOf[Val]

  val WAY4: Val = Val({ propsModel => new Way4UaspDtoParser(propsModel) })
  val MDM = Val({ propsModel => new MDMUaspDtoParser(propsModel) })
  val MDM_PROFILE = Val({ propsModel => new MDMUaspDtoParser(propsModel) })
  val ISSIUNG_CLIENT = Val({ propsModel => new  ClientUaspDtoParser(propsModel) })
  val ISSUING_ACCOUNT_BALANCE = Val({ propsModel => new  AccountBalanceUaspDtoParser(propsModel) })
  val ISSUING_ACCOUNT = Val({ propsModel => new  AccountUaspDtoParser(propsModel) })
  val ISSUING_CARD = Val({ propsModel => new  CardUaspDtoParser(propsModel) })
  val CURRENCY = Val({ propsModel => new  CurrencyUaspDtoParser(propsModel) })
  val FIRST_SALARY = Val({ propsModel => new  FirstSalaryUaspDtoParser(propsModel) })
  val CA_FIRST_SALARY = Val({ propsModel => new  CAFirstSalaryUaspDtoParser(propsModel) })
  val POS_TRANSACTION = Val({ propsModel => new  Way4UaspDtoParser(propsModel) })
  val WAY4_FIRST_SALARY = Val({ propsModel => new  Way4UaspDtoParser(propsModel) })
  val PROFILE = Val({ propsModel => new  ProfileUaspDtoParser(propsModel) })
  val PROFILE_AUTH = Val({ propsModel => new  ProfileAuthUaspDtoDaoParser(propsModel) })
  val WITHDRAW = Val({ propsModel => new  WithDrawUaspDtoParser(propsModel) })
  val WAY4_WITHDRAW = Val({ propsModel => new  Way4UaspDtoParser(propsModel) })
  val CARDFL = Val({ propsModel => new  CardFlUaspDtoParser(propsModel) })
  val CA_CARDFL = Val({ propsModel => new  CaCardFlUaspDtoParser(propsModel) })
  val CA_DEPOSITFL = Val({ propsModel => new  CaDepositFlUaspDtoParser(propsModel) })
  val CUSTOMER_PACKAGE = Val({ propsModel => new  CustomerPackageUaspDtoStandart(propsModel) })
  val CUSTOMER_PROFILE_FULL = Val({ propsModel => new  CustomerProfileFullUaspDtoStandart(propsModel) })
  val UDDS = Val({ propsModel => new  UddsUaspDtoParser(propsModel) })
  val LOYALTY = Val({ propsModel => new  LoyaltyUaspDtoParser(propsModel) })

}

