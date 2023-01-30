package ru.vtb.uasp.inputconvertor.service

import io.qameta.allure.scalatest.AllureScalatestContext
import org.json4s.jackson.JsonMethods.{compact, pretty, render}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.entity.InputMessageType

import scala.collection.mutable

class TransformHelperTest extends AnyFlatSpec with should.Matchers {

  "extractJson type mdm" should "be return 4 messages" in new AllureScalatestContext {
    val allProps = getAllProps(args = Array("--input-convertor.json.split.element", "contact"), "application-mdm.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    val defaultJsonSchemaKey = getSchemaKey(allProps)
    val msgCollector = new MsgCollector
    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val result = TransformHelper.extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val doc1 = render(msgCollector.getAll().get(1).json_message.get)
    val compactJson1 = compact(doc1)
    val prettyJson1 = pretty(doc1)
    println(prettyJson1)

    msgCollector should not be (null)
    msgCollector.getAll().size() shouldEqual 4
  }

  "extractJson type mdm" should "be return 1 messages" in new AllureScalatestContext {
    val allProps = getAllProps(args = Array.empty, "application-mdm.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    val defaultJsonSchemaKey = getSchemaKey(allProps)
    val msgCollector = new MsgCollector
    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println(inMessage)
    val result = TransformHelper.extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val doc1 = render(msgCollector.getAll().get(0).json_message.get)
    val compactJson1 = compact(doc1)
    val prettyJson1 = pretty(doc1)
    println(compactJson1)

    msgCollector should not be (null)
    msgCollector.getAll().size() shouldEqual 1
  }

  "extractJson type mdm" should "be throw exception" in new AllureScalatestContext {
    val allProps = getAllProps(args = Array("--input-convertor.json.split.element", "contact"), "application-mdm.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    val defaultJsonSchemaKey = getSchemaKey(allProps)
    val msgCollector = new MsgCollector
    val jsonMessageStr = "test:invalid"
    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val result = TransformHelper.extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)

    println(msgCollector.getAll().get(0).error.get)

    msgCollector should not be (null)
    msgCollector.getAll().size() shouldEqual 1
    msgCollector.getAll().get(0).error.get should startWith ("Error json parsing: Unrecognized token 'test':")
  }
  "extractJson type currency" should "be return 40 messages" in new AllureScalatestContext {
    val allProps = getAllProps(args = Array("--input-convertor.json.split.element", "rates"), "application-currency.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    val defaultJsonSchemaKey = getSchemaKey(allProps)
    val msgCollector = new MsgCollector
    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val result = TransformHelper.extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val doc1 = render(msgCollector.getAll().get(1).json_message.get)
    val compactJson1 = compact(doc1)
    val prettyJson1 = pretty(doc1)
    println(prettyJson1)

    msgCollector should not be (null)
    msgCollector.getAll().size() shouldEqual 21
  }
}
