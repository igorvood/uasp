package ru.vtb.uasp.inputconvertor.utils.json

import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.networknt.schema._

import java.util
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object JsonUtils {
  val mapper = new ObjectMapper() // with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
//  mapper.registerModule(new JavaTimeModule())
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)



  def toJsonNode(json: String): JsonNode = mapper.readTree(json)
  def JsonToByteArray(value: Any): Array[Byte] = mapper.writeValueAsBytes(value)

  def getJsonSchemaFromJsonNode(schemaContentJN: JsonNode, specVersionStr: String): JsonSchema   = {
    val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.valueOf(specVersionStr))
    factory.getSchema(schemaContentJN)
  }

  def getJsonSchemaFromJsonNode(schemaContentJN: JsonNode): JsonSchema = {
    val factory =  JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaContentJN))
    factory.getSchema(schemaContentJN)
  }

//  def checkValidJsonMessage(message: String, jsonSchemaStr: String, specVersionStr: String): Boolean = {
//    val jsonSchema = toJsonNode(jsonSchemaStr)
//    checkValidJsonMessage(message, jsonSchema, specVersionStr)
//  }

  def checkValidJsonMessage(jn: JsonNode, jsonSchemaStr: String,
                            specVersionStr: String
                           ): Boolean = {
    val jsonSchema = toJsonNode(jsonSchemaStr)
    val schema: JsonSchema = if (specVersionStr != "") getJsonSchemaFromJsonNode(jsonSchema, specVersionStr) else getJsonSchemaFromJsonNode(jsonSchema)
    val errors: util.Set[ValidationMessage] = schema.validate(jn)
    val error = errors.map(_.getMessage).reduceOption{ (acc, elem) => acc + "|" + elem}.getOrElse("")
    if (error != "") throw new RuntimeException(error)
    return true

    false
  }

//  def checkValidJsonMessage(message: String, jsonSchema: JsonNode,
//                            specVersionStr: String
//                           ): Boolean = {
//    val jn: JsonNode = toJsonNode(message)
//    checkValidJsonMessage(jn, jsonSchema, specVersionStr)
//  }

}
