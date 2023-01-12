package ru.vtb.bevent.first.salary.aggregate.entity

case class PaymentInfo(paymentSum: Option[BigDecimal], date: Long)

case class CountsAggregate(dates: Option[Seq[PaymentInfo]], datesWithMap: Option[Map[String, Seq[PaymentInfo]]])
