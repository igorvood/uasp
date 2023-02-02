package ru.vtb.uasp.inputconvertor.service

import com.sksamuel.avro4s.AvroSchema
import io.qameta.allure.Allure
import io.qameta.allure.scalatest.AllureScalatestContext
import org.apache.avro.Schema
import org.scalatest.Ignore
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.MDMUaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.utils.avro.AvroUtils
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

//FIXME
@Ignore
class MDMConvertorHelperTest extends AnyFlatSpec with should.Matchers {

  "The serialized avro object" should "be equals standard serialized mdm UaspDto" in new AllureScalatestContext {
    Allure.link("302202", "manual", "")
    Allure.tms("22", "")

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = MDMUaspDtoDaoTest.getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val jsonSchema = getStringFromResourceFile("schemas/jsonschema-" + uaspDtoType + ".json")
    val enrichedCommonMessage = commonMessage.copy(json_schema = Some(jsonSchema))
    val propsModel: InputPropsModel = null // InputPropsModel(Map("input-convertor.uaspdto.type" -> uaspDtoType), "")

    val testedMessage: CommonMessageType = ConvertHelper.validAndTransform(enrichedCommonMessage, propsModel, appUseAvroSerializationIsY = true, droolsValidator, dtoMap)
    println("testedMessage: " + testedMessage)

    val initialUaspDto: UaspDto = AvroUtils.avroDeserialize[UaspDto](testedMessage.avro_message.get)
    //standard
    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto(initialUaspDto.uuid)
    assert(standardUaspDto == initialUaspDto.copy(process_timestamp = 0))
  }
}

