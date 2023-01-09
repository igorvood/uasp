package ru.vtb.uasp.pilot.model.vector.dao

import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.json.JsonUtil.{getAllCasesFields, getFieldsForCases}
import ru.vtb.uasp.pilot.model.vector.dao.Generator


object TestUtil {

  val fieldsFullVector: Map[String, Map[String, String]] = getFieldsForCases()("DKO_W4_full_model_vector")
  val fields2_2: Map[String, Map[String, String]] = getFieldsForCases()("DKO_way4-case-2-2")
  val fields2_3:Map[String, Map[String, String]] = getFieldsForCases()("DKO_way4-case-2-3")
  val allFields:Map[String, Map[String, String]] = getAllCasesFields(getFieldsForCases())

  val inHaStaticFullVector: UaspDto = Generator.createTestObjectStatic(1L, "ha", fieldsFullVector).copy(id=null)
  val inCaStaticFullVector: UaspDto = Generator.createTestObjectStatic(1L, "ca", fieldsFullVector).copy(id=null)

  val inHaStatic2_2: UaspDto = Generator.createTestObjectStatic(1L, "ha", fields2_2)
  val inCaStatic2_2: UaspDto = Generator.createTestObjectStatic(1L, "ca", fields2_2)
  val inHaStatic2_3: UaspDto = Generator.createTestObjectStatic(1L, "ha", fields2_3)
  val inCaStatic2_3: UaspDto = Generator.createTestObjectStatic(1L, "ca", fields2_3)

  val inNull: UaspDto = UaspDto("1", Map(), Map(), Map(), Map(), Map(), Map(), Map(), "uuid", 1L)

  val streamCaType = "ca"
  val streamHaType = "ha"

  val inHa2_2: UaspDto = Generator.createTestObject(1L, "ha", fields2_2)
  val inCa2_2: UaspDto = Generator.createTestObject(1L, "ca",fields2_2)

  val inHa2_3: UaspDto = Generator.createTestObject(1L, "ha", fields2_3)
  val inCa2_3: UaspDto = Generator.createTestObject(1L, "ca",fields2_3)

  val inCaFullVector: UaspDto = Generator.createTestObject(1L, "ca",fieldsFullVector)
  val inHaFullVector: UaspDto = Generator.createTestObject(1L, "ha",fieldsFullVector)

  val modelVectorDao: ModelVectorDao = new ModelVectorDao

}
