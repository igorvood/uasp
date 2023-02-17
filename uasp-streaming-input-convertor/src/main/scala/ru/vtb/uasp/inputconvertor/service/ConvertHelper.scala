package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import org.json4s.JValue
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.service.dto.UaspAndKafkaKey
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.util.{Failure, Success, Try}

object ConvertHelper {


//  @deprecated
//  def validAndTransform(commonMessage: CommonMessageType,
//                        propsModel: InputPropsModel
//                       ): CommonMessageType = {
//    if (!commonMessage.valid) return commonMessage
//    val cm = commonMessage.copy(valid = false)
//
//    val parser = propsModel.uaspDtoParser
//    val uaspDto: UaspDto = Try(parser.fromJValue(cm.json_message.get, propsModel.dtoMap)) match {
//      case Success(s) => s
//      case Failure(s) => return cm.copy(error = Some("Cant create UaspDto from json: " + s.getMessage))
//    }
//
//    //3. drools check
//    val droolsErrors = propsModel.droolsValidator.validate(List(uaspDto))
//    if (droolsErrors.nonEmpty) return cm.copy(error = Some("Drools validation error: " + droolsErrors.map(_.msg).mkString("; ")))
//
//
//    val outBytes1: Array[Byte] = {
//      // TODO переработать
//      JsonConvertOutService.serializeToBytes(uaspDto, None) match {
//        case Left(s) => return cm.copy(error = Some("Can't serialize to avro format: " + s.map(_.error).mkString("\n")))
//        case Right(value) => value.value
//      }
//
//    }
//
//    cm.copy(error = None, valid = true, outBytes = Some(outBytes1))
//
//  }
}

class Validate(val propsModel: InputPropsModel) extends DlqProcessFunction[JValue, UaspAndKafkaKey, OutDtoWithErrors[JValue]] {



//  override def processWithDlq(dto: JValue): Either[OutDtoWithErrors[JValue], UaspAndKafkaKey] = {
//    val parser = propsModel.uaspDtoParser
//    val uaspDtoOrErr= Try(parser.fromJValue(dto, propsModel.dtoMap)) match {
//      case Success(s) => {
//        propsModel.droolsValidator.validate(List(s)) match {
//          case q if q.isEmpty => Right(s)
//          case w => Left(OutDtoWithErrors[JValue](propsModel.serviceData, Some(this.getClass.getName), "Drools validation error: " :: w.map(e => e.msg), Some(dto)))
//        }
//      }
//      case Failure(s) => Left(OutDtoWithErrors[JValue](propsModel.serviceData, Some(this.getClass.getName), List("Cant create UaspDto from json: " + s.getMessage), Some(dto)))
//    }
//
//    uaspDtoOrErr
//  }

  override def processWithDlq(dto: JValue): Either[OutDtoWithErrors[JValue], UaspAndKafkaKey] = ???
}