package ru.vtb.uasp.inputconvertor.factory

import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

package object UaspdtoTypeEnum extends Enumeration {
  case class Val(parser: InputPropsModel => UaspDtoParser) extends super.Val

  val WAY4: Val = Val({ propsModel => new Way4UaspDtoParser(propsModel) })
  val MDM = Val({ propsModel => new MDMUaspDtoParser(propsModel) })
  val MDM_PROFILE = Val({ propsModel => new MDMUaspDtoParser(propsModel) })
  val ISSIUNG_CLIENT = Val({ propsModel => new ClientUaspDtoParser(propsModel) })
  val ISSUING_ACCOUNT_BALANCE = Val({ propsModel => new AccountBalanceUaspDtoParser(propsModel) })
  val ISSUING_ACCOUNT = Val({ propsModel => new AccountUaspDtoParser(propsModel) })
  val ISSUING_CARD = Val({ propsModel => new CardUaspDtoParser(propsModel) })
  val CURRENCY = Val({ propsModel => new CurrencyUaspDtoParser(propsModel) })
  val FIRST_SALARY = Val({ propsModel => new FirstSalaryUaspDtoParser(propsModel) })
  val CA_FIRST_SALARY = Val({ propsModel => new CAFirstSalaryUaspDtoParser(propsModel) })
  val POS_TRANSACTION = Val({ propsModel => new Way4UaspDtoParser(propsModel) })
  val WAY4_FIRST_SALARY = Val({ propsModel => new Way4UaspDtoParser(propsModel) })
  val PROFILE = Val({ propsModel => new ProfileUaspDtoParser(propsModel) })
  val PROFILE_AUTH = Val({ propsModel => new ProfileAuthUaspDtoDaoParser(propsModel) })
  val WITHDRAW = Val({ propsModel => new WithDrawUaspDtoParser(propsModel) })
  val WAY4_WITHDRAW = Val({ propsModel => new Way4UaspDtoParser(propsModel) })
  val CARDFL = Val({ propsModel => new CardFlUaspDtoParser(propsModel) })
  val CA_CARDFL = Val({ propsModel => new CaCardFlUaspDtoParser(propsModel) })
  val CA_DEPOSITFL = Val({ propsModel => new CaDepositFlUaspDtoParser(propsModel) })
  val CUSTOMER_PACKAGE = Val({ propsModel => new CustomerPackageUaspDtoStandart(propsModel) })
  val CUSTOMER_PROFILE_FULL = Val({ propsModel => new CustomerProfileFullUaspDtoStandart(propsModel) })
  val UDDS = Val({ propsModel => new UddsUaspDtoParser(propsModel) })
  val LOYALTY = Val({ propsModel => new LoyaltyUaspDtoParser(propsModel) })

}
