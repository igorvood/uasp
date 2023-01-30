package ru.vtb.uasp.validate

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.Convertor.{outputTag, process}
import ru.vtb.uasp.inputconvertor.dao.Way4UaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.entity.{InputMessageType, InputSchemaType}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel


class ConvertorFullTest extends AnyFlatSpec with should.Matchers {

  "The valid UaspDto message" should "return empty error list" in {

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = Way4UaspDtoDaoTest.getCommonMessageAndProps()
    val allPropsNew = allProps.map(q => q._1.replace("app", "input-convertor.app") -> q._2)

    val jsonSchema: String = getStringFromResourceFile("schemas/jsonschema-" + uaspDtoType + ".json")
    val specJsonVersion: String = allPropsNew.getOrElse("app.json.schema.version", "")
    val inputMessageType = InputMessageType(commonMessage.message_key, commonMessage.message, Map())


    val propsModel = InputPropsModel(allPropsNew, "app")


    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val jsonSchemaInputStream = env.fromCollection(List(InputSchemaType(specJsonVersion, jsonSchema)))
    val messageInputStream = env.fromCollection(List(inputMessageType))

    val mainDataStream = process(jsonSchemaInputStream, messageInputStream, propsModel)
//    mainDataStream
//      .getSideOutput(outputTag)
//      .map("qweqweqwewqeqw"->_)
//      .print()

    mainDataStream
      .map("asdklasjhdlaskjhdaslkjdh"->_)
      .print()

    env.execute("executionEnvironmentProperty.appServiceName")


  }


}


object ConvertorFullTest {

}