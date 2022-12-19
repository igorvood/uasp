package ru.vtb.uasp.mutator.way4.abstraction

sealed trait TestAction

case class NoneTestAction() extends TestAction

case class AddTestAction(v: String) extends TestAction

case class DeleteTestAction() extends TestAction

case class MutateTestAction(v: String) extends TestAction
