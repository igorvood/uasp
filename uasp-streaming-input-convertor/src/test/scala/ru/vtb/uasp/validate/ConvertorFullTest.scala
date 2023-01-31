package ru.vtb.uasp.validate

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.inputconvertor.Convertor.process
import ru.vtb.uasp.inputconvertor.dao.Way4UaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel


class ConvertorFullTest extends AnyFlatSpec with should.Matchers {

  "The valid UaspDto message" should "return empty error list" in {

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = Way4UaspDtoDaoTest.getCommonMessageAndProps()

    val inputMessageType = InputMessageType(commonMessage.message_key, commonMessage.message, Map())


    val propsModel = new NewInputPropsModel(
      appServiceName = null,
      appUaspdtoType = null,
      appInputTopicName = null,
      appOutputTopicName = null,
      appDlqTopicName = null,
      appUseAvroSerialization = false,
      appSavepointPref = null,
      dtoMap = null,
      appReadSourceTopicFrombeginning = false,
      SHA256salt = null,
      messageJsonPath = None,
      jsonSplitElement = None)


    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val messageInputStream = env.fromCollection(List(inputMessageType))

    val mainDataStream = process(messageInputStream, propsModel)

    mainDataStream
      .map("asdklasjhdlaskjhdaslkjdh" -> _)
      .print()

    env.execute("executionEnvironmentProperty.appServiceName")


  }


}

