package ru.vtb.uasp.validate

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong


class DateConvertTest extends AnyFlatSpec with should.Matchers {

  "The valid UaspDto message" should "return empty error list" in new AllureScalatestContext {
    private val dateStrUtc = "2022-08-26T09:41:37Z"
    private val dateMsk = "2022-08-26T12:41:37Z"

    private val ffffff = "yyyy-MM-dd'T'HH:mm:ss"
    private val l: Long = dtStringToLong(dateMsk, ffffff, "GMT+3")

    private val dateLongUtc: Long = dtStringToLong(dateStrUtc, "yyyy-MM-dd'T'HH:mm:ss", "UTC")
    private val dateLongMsk: Long = dtStringToLong(dateMsk, ffffff, "UTC")
    private val dateLongMskUtc: Long = dtStringToLong(dateMsk, ffffff, "GMT+0300")
    private val dateLongMskUtc1: Long = dtStringToLong(dateMsk, ffffff, "GMT+0000")

    println(dateLongUtc)
    println(dateLongMsk)

    println(dateLongMsk - dateLongUtc)
    println(dateLongMskUtc - dateLongUtc)
    println(dateLongMskUtc1 - dateLongUtc)


  }


}