package ru.vtb.bevent.first.salary.aggregate.entity

object CountsType extends Enumeration {
  type Count = Value

  val Salary, Pens, Pos, Account, PosByCard, FirstSalaryForAccount = Value
}
