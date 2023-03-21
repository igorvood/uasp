package ru.vtb.uasp.inputconvertor.factory

import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao._
import ru.vtb.uasp.inputconvertor.dao.ca._
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.language.implicitConversions


trait UaspDtoParser extends Serializable {

  val propsModel: InputPropsModel

  def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]]
}

class Way4UaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    Way4UaspDtoDao.fromJValue(mes, dtoMap)
}

// TODO Получить json
class MDMUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    MDMUaspDtoDao.fromJValue(mes, dtoMap, propsModel.uaspdtoType)
}

class ClientUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    IssuingClientUaspDtoDao.fromJValue(mes, dtoMap)
}

class AccountBalanceUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    IssuingAccountBalanceUaspDtoDao.fromJValue(mes, dtoMap)
}

class AccountUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    IssuingAccountUaspDtoDao.fromJValue(mes, dtoMap)
}

class CardUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    IssuingCardUaspDtoDao.fromJValue(mes, dtoMap)
}

// TODO Получить json
class CurrencyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CurrencyUaspDtoDao.fromJValue(mes, dtoMap)
}

class FirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    FirstSalaryUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class CAFirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CAFirstSalaryUaspDtoDao.fromJValue(mes, dtoMap)
}

class ProfileUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser with Serializable {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    ProfileUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class ProfileAuthUaspDtoDaoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    ProfileAuthUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class WithDrawUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    WithdrawUaspDtoDao.fromJValue(mes, dtoMap)
}

class CardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

class CaCardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CACardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

class CaDepositFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CADepositFlUaspDtoDao.fromJValue(mes, dtoMap)
}


class CustomerPackageUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CustomerPackageUaspDtoDao.fromJValue(mes, dtoMap)
}

class CustomerProfileFullUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    CustomerIdProfileFullUaspDtoDao.fromJValue(mes, dtoMap)
}

class UddsUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    UddsUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class LoyaltyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] =
    LoyaltyUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

object UaspDtoParserFactory {
  def apply(propsModel: InputPropsModel) = propsModel.uaspdtoType.toUpperCase() match {
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


