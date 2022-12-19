package ru.vtb.uasp.mutator.way4.abstraction

sealed trait TestCaseData {
  val nameField: String
  val action: TestAction
  val expecped: Option[String] = None
}

case class TestInt(
                    private val nameFld: String,
                    private val act: TestAction,
                    private val exp: Option[String] = None,

                  ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestLong(
                     private val nameFld: String,
                     private val act: TestAction,
                     private val exp: Option[String] = None,

                   ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestFloat(
                      private val nameFld: String,
                      private val act: TestAction,
                      private val exp: Option[String] = None,

                    ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestDouble(
                       private val nameFld: String,
                       private val act: TestAction,
                       private val exp: Option[String] = None,

                     ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestBigDecimal(
                           private val nameFld: String,
                           private val act: TestAction,
                           private val exp: Option[String] = None,

                         ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestString(
                       private val nameFld: String,
                       private val act: TestAction,
                       private val exp: Option[String] = None,

                     ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}

case class TestBoolean(
                        private val nameFld: String,
                        private val act: TestAction,
                        private val exp: Option[String] = None,

                      ) extends TestCaseData {
  override val nameField: String = nameFld
  override val action: TestAction = act
  override val expecped: Option[String] = exp
}