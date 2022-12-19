package ru.vtb.uasp.common.tests

import org.scalatest.Suites
import ru.vtb.uasp.common.service.JsonConverterTest

class AggregateTestSuites extends Suites {
  (new JsonConverterTest).execute()

}
