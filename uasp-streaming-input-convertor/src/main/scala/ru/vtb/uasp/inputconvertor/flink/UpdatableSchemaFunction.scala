package ru.vtb.uasp.inputconvertor.flink

import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputSchemaType}


class UpdatableSchemaFunction(defaultJsonSchemaKey: String, staticJsonSchema: String) extends BroadcastProcessFunction[CommonMessageType, InputSchemaType, CommonMessageType] {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  private lazy val schemasStateDescriptor =
    new MapStateDescriptor[String, String]("issuing-operation", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)

  override def processElement(message: CommonMessageType,
                              readOnlyCtx: BroadcastProcessFunction[CommonMessageType, InputSchemaType, CommonMessageType]#ReadOnlyContext,
                              out: Collector[CommonMessageType]): Unit = {
    val collectResult: CommonMessageType =
      if (!message.valid) message
      else {
        val schemas = readOnlyCtx.getBroadcastState(schemasStateDescriptor)
        val schemaKey = message.json_schemakey.get
        if (schemas.contains(schemaKey)) {
          val currentSchema = schemas.get(schemaKey)
          message.copy(json_schema = Some(currentSchema))
        } else {
          logger.info("Waiting broadcast stream ...")
          Thread.sleep(5000)
          logger.info(s"For message ${message.message_key} will be apply static json schema $defaultJsonSchemaKey instead ${message.json_schemakey.get}. Check right schema in kafka topic")
          message.copy(json_schema = Some(staticJsonSchema))
        }
      }
    out.collect(collectResult)

  }

  override def processBroadcastElement(schema: InputSchemaType,
                                       ctx: BroadcastProcessFunction[CommonMessageType, InputSchemaType, CommonMessageType]#Context,
                                       out: Collector[CommonMessageType]): Unit = {
    val schemas = ctx.getBroadcastState(schemasStateDescriptor)
    schemas.put(schema.schema_key, schema.schema)
  }
}


