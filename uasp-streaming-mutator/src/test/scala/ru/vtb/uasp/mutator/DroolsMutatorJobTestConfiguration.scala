package ru.vtb.uasp.mutator

import ru.vtb.uasp.common.dto.UaspDto

import java.util.Calendar

object DroolsMutatorJobTestConfiguration {
  val uaspDtoWithFilelds: Array[UaspDto] = (2 to 2)
    .map(q => UaspDto(id = "id",
      dataInt = Map("someInt" -> q),
      dataLong = Map("someLong" -> q),
      dataFloat = Map("someFloat" -> q),
      dataDouble = Map("someDouble" -> q),
      dataDecimal = Map("someBigDecimal" -> BigDecimal(q)),
      dataString = Map("someString" -> q.toString),
      dataBoolean = Map("someBoolean" -> {
        val bool = if (q % 2 == 1) true else false
        bool
      }
      ),
      uuid = "String",
      process_timestamp = Calendar.getInstance.getTimeInMillis)).toArray
}
