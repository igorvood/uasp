package ru.vtb.ie.scenarios

import java.util.Calendar
import scala.collection.immutable

object UserIds {
  val localCustomerId = "local_customer_id"
  private val prefix = Calendar.getInstance().getTimeInMillis

  val ids: Int => immutable.Seq[String] = { cnt =>
//    val strings = for (i <- 1 to cnt) yield (/*prefix + */222).toString
    val strings = for (i <- 1 to cnt) yield (prefix +i).toString
    strings
  }

}
