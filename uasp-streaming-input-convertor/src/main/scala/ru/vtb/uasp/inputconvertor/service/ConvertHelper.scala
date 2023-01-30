package ru.vtb.uasp.inputconvertor.service

import org.apache.avro.Schema
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

import scala.util.{Failure, Success, Try}

object ConvertHelper {


  def validAndTransform(commonMessage: CommonMessageType,
                        propsModel: NewInputPropsModel,
                        appUseAvroSerializationIsY: Boolean,
                        validator: DroolsValidator,
                        avroSchema: Schema,
                        dtoMap: Map[String, Array[String]],

                        convertOutMapService: ConvertOutMapService): CommonMessageType = {
    val appUaspdtoType: String = propsModel.appUaspdtoType
    if (!commonMessage.valid) return commonMessage
    val cm = commonMessage.copy(valid = false)
    //1. check schema compliance
    //time("3. check schema compliance") {
    //FIXME !
    /*Try(JsonUtils.checkValidJsonMessage(asJsonNode(cm.json_message.get), cm.json_schema.get, specJsonVersionStr)) match {
      case Success(_) =>
      case Failure(s) => return cm.copy(error = Some("The message does not match schema: " + s.getMessage))
    }*/
    //}

    //2. create uaspdto
    val uaspDtoParser = UaspDtoParserFactory(appUaspdtoType, propsModel)
    val uaspDto: UaspDto = Try(uaspDtoParser.fromJValue(cm.json_message.get, dtoMap)) match {
      case Success(s) => s
      case Failure(s) => return cm.copy(error = Some("Cant create UaspDto from json: " + s.getMessage))
    }
    //}
    //logger.info ( "UaspDto: " + uaspDto )

    //3. drools check
    val droolsErrors = validator.validate(List(uaspDto))
    if (droolsErrors.nonEmpty) return cm.copy(error = Some("Drools validation error: " + droolsErrors.map(_.msg).mkString("; ")))

    //4. to avro
    //fixme 10.10.2022 сериализация в json, сериализация UASPDTO в avro закомментирована
    /*val avroMessage: Array[Byte] = if (appUseAvroSerializationIsY)
      Try(convertOutMapService.serialize(uaspDto)) match {
        case Success(s) => s._2
        case Failure(s) => return cm.copy(error = Some("Can't serialize to avro format: " + s.getMessage))
      }
    else cm.avro_message.getOrElse(new Array[Byte](0))*/

    val avroMessage: Array[Byte] = if (appUseAvroSerializationIsY)
      Try(JsonConvertOutService.serializeToBytes(uaspDto)) match {
        case Success(s) => s.value
        case Failure(s) => return cm.copy(error = Some("Can't serialize to avro format: " + s.getMessage))
      }
    else cm.avro_message.getOrElse(new Array[Byte](0))

    //    val avroMessage : Array [ Byte ] =
    //    //time("4. to avro") {
    //      Try ( AvroUtils.avroSerialize [ UaspDto ]( uaspDto ) ) match {
    //        case Success ( s ) => s
    //        case Failure ( s ) => return cm.copy ( error = Some ( "Can't serialize to avro format: " + s.getMessage ) )
    //      }
    //}
    //val avroMessage = Array[Byte]()


    cm.copy(error = None, valid = true, avro_message = Some(avroMessage))

  }
}
