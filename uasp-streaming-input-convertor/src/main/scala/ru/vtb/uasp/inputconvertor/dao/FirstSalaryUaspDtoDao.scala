package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, getMapO, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.FirstSalaryUaspDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object FirstSalaryUaspDtoDao {

  lazy val systemSource = "CFT2RS"

  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value1 = inMessage.validate[FirstSalaryUaspDto]
      .map { dj =>
        UaspDto(
          id = dj.clientId.orNull,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map.empty,
          dataLong = getMapO[Long](dtoMap("app.uaspdto.fields.CFT2RS_CD.eventTime")(0), dj.eventTime),
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.operationAmount.sum")(0), dj.data.flatMap(_.operationAmount.flatMap(_.sum)).orNull),
            getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.feeAmount.sum")(0), dj.data.flatMap(_.feeAmount.flatMap(_.sum)).orNull),
            getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.transactionAmount.sum")(0), dj.data.flatMap(_.transactionAmount.flatMap(_.sum)).orNull)
          ),
          dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.source_system")(0), systemSource),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.clientId")(0), dj.clientId.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.local_id")(0), dj.internalId.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.KBO")(0), dj.data.flatMap(_.KBO).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.account")(0), dj.data.flatMap(_.account).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.systemId")(0), dj.systemId.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDepType")(0), dj.vidDepType.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDepName")(0), dj.vidDepName.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.operationAmount.currency")(0), dj.data.flatMap(_.operationAmount.flatMap(_.currency)).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.data_status")(0), dj.data.flatMap(_.status).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.vidDep")(0), dj.vidDep.orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.data_transactionDate")(0), dj.data.flatMap(_.transactionDate).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.empty.string.hash")(0), HashUtils.getHashSHA256PrependingSalt("", propsModel.sha256salt)),
            getMapEntry[String](dtoMap("app.uaspdto.fields.data_operationName")(0), dj.data.flatMap(_.operationName).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.transactionId")(0), dj.transactions.headOption.map(q => q.transactionId).getOrElse("")),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.feeAmount.currency")(0), dj.data.flatMap(_.feeAmount.flatMap(_.currency)).orNull),
            getMapEntry[String](dtoMap("app.uaspdto.fields.CFT2RS_CD.data.transactionAmount.currency")(0), dj.data.flatMap(_.transactionAmount.flatMap(_.currency)).orNull)
          ),
          dataBoolean = Map.empty ++ getMapO[Boolean](dtoMap("app.uaspdto.fields.CFT2RS_CD.firstAdd")(0), dj.firstAdd) ++
            getMapO[Boolean](dtoMap("app.uaspdto.fields.data_debet")(0), dj.data.flatMap(_.debet))

        )
      }
      .map(d => Json.toJson(d))

    List(value1)

  }


}
