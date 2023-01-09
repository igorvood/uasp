package ru.vtb.bevent.first.salary.aggregate.entity

object FieldNamesConst {

  //w4 income
  val KBO = "kbo_w4"
  val PROCESSING_DATETIME = "processing_datetime"
  val SOURCE_SYSTEM_W4 = "source_system_w4"
  val BASE_AMOUNT_W4 = "base_amount_w4"
  val CURRENCY_SCALE = "currency_scale"
  val CURRENCY_PRICE = "currency_price"
  val TRANSACTION_DATETIME = "transaction_datetime"
  val BASE_CURRENCY_W4 = "base_currency_w4"
  val ACTION_TYPE = "action_type"
  val SOURCE_ACCOUNT_W4 = "source_account_w4"

  //common names
  val AMOUNT = "amount"
  val IS_INCOME = "is_income"
  val COMMON_KBO = "kbo"
  val SOURCE_ACCOUNT = "source_account"
  val ACCOUNT_TYPE = "account_type"
  val EVENT_DTTM = "event_dttm"
}
