package ru.vtb.uasp.inputconvertor.factory

import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao._
import ru.vtb.uasp.inputconvertor.dao.ca._
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.language.implicitConversions


trait UaspDtoParser extends Serializable {

  val propsModel: InputPropsModel

  def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]]
}

class Way4UaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    Way4UaspDtoDao.fromJValue(mes, dtoMap)
}

// TODO Получить json
class MDMUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    MDMUaspDtoDao.fromJValue(mes, dtoMap, propsModel.uaspdtoType)
}

class ClientUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    IssuingClientUaspDtoDao.fromJValue(mes, dtoMap)
}

class AccountBalanceUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    IssuingAccountBalanceUaspDtoDao.fromJValue(mes, dtoMap)
}

class AccountUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    IssuingAccountUaspDtoDao.fromJValue(mes, dtoMap)
}

class CardUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    IssuingCardUaspDtoDao.fromJValue(mes, dtoMap)
}

class CloseAccountDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CloseAccountDao.fromJValue(mes, dtoMap)
}

class CurrencyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CurrencyUaspDtoDao.fromJValue(mes, dtoMap)
}

class FirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    FirstSalaryUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class CAFirstSalaryUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CAFirstSalaryUaspDtoDao.fromJValue(mes, dtoMap)
}

class ProfileUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser with Serializable {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    ProfileUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class ProfileAuthUaspDtoDaoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    ProfileAuthUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class WithDrawUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    WithdrawUaspDtoDao.fromJValue(mes, dtoMap)
}

class CardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

class CaCardFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CACardFlUaspDtoDao.fromJValue(mes, dtoMap)
}

class CaDepositFlUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CADepositFlUaspDtoDao.fromJValue(mes, dtoMap)
}


class CustomerPackageUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CustomerPackageUaspDtoDao.fromJValue(mes, dtoMap)
}

class CustomerProfileFullUaspDtoStandart(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    CustomerIdProfileFullUaspDtoDao.fromJValue(mes, dtoMap)
}

class UddsUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
    UddsUaspDtoDao.fromJValue(mes, propsModel, dtoMap)
}

class LoyaltyUaspDtoParser(override val propsModel: InputPropsModel) extends UaspDtoParser {
  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] =
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
    case "CLOSE_ACCOUNT" => new CloseAccountDtoParser(propsModel)
    case _ => throw new RuntimeException("Wrong UaspDto type: " + propsModel.uaspdtoType)

  }
}


