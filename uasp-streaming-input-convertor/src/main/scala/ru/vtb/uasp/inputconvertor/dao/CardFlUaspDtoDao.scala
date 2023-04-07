package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, getMapO, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.{CardFlUaspDtoP1, CardFlUaspDtoP2, CardFlUaspDtoP3}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CardFlUaspDtoDao {

  lazy val systemSource = "cardfl"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value = for {
      p1 <- inMessage.validate[CardFlUaspDtoP1]
      p2 <- inMessage.validate[CardFlUaspDtoP2]
      p3 <- inMessage.validate[CardFlUaspDtoP3]
    } yield
      Json.toJson(
        UaspDto(
        id = p1.mdmid,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Cnt")(0), p2.RtSpnd30Cnt) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Cnt")(0), p2.RtSpnd60Cnt) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.crtXzKkTpCd")(0), p1.CrtXzKkTpCdInt) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Cnt")(0), p2.RtSpnd90Cnt)
        ,
        dataLong = Map.empty ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.bnsCtgChDt")(0), p1.BnsCtgChDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.pkgClsDt")(0), p2.PkgClsDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.pkgStrtDt")(0), p2.PkgStrtDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnLstSsnDt")(0), p2.VtbOnlnLstSsnDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.zFrntClntId")(0), p3.ZFrntClntId)
        ,
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = mapCollect[BigDecimal](
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.bnsBlncAmt")(0), p1.BnsBlncAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt1MAmt")(0), p1.CrdCrdt1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt2MAmt")(0), p1.CrdCrdt2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt3MAmt")(0), p1.CrdCrdt3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt1MAmt")(0), p1.CrdDbt1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt2MAmt")(0), p1.CrdDbt2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt3MAmt")(0), p1.CrdDbt3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch1MAmt")(0), p1.CrdPrch1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch2MAmt")(0), p1.CrdPrch2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch3MAmt")(0), p1.CrdPrch3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn1MCnt")(0), p1.CrdTxn1MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn2MCnt")(0), p1.CrdTxn2MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn3MCnt")(0), p2.CrdTxn3MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.incmCnfAmtNc")(0), p2.IncmCnfAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.invstPrtfAmtNc")(0), p2.InvstPrtfAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Amt")(0), p2.RtSpnd30Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Amt")(0), p2.RtSpnd60Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Amt")(0), p2.RtSpnd90Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttAnnAmtNc")(0), p2.TtAnnAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttBlncAmt")(0), p2.TtBlncAmt.orNull[BigDecimal])),
        dataString = mapCollect[String](getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.mdmid")(0), p1.mdmid),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.crtXzFdTpCd")(0), p1.CrtXzFdTpCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.packDsc")(0), p1.PackDsc.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.system_source")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.bnsCtgCd")(0), p1.BnsCtgCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.cntrCd")(0), p1.CntrCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.gndrCd")(0), p2.GndrCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.rgnCd")(0), p2.RgnCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.actCatMDMCd")(0), p3.ActCatMDMCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.srvcMdl")(0), p3.SrvcMdl.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.pfmManSapId")(0), p3.PfmManSapId.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.intOrgGenId")(0), p3.IntOrgGenId.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.zfrntChngCd")(0), p3.ZfrntChngCd.orNull[String])
        ),
        dataBoolean = Map.empty ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.chStLCFlg")(0), p1.ChStLCFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.ctprtFlg")(0), p2.CtprtFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.ipFlg")(0), p2.IpFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.prspFlg")(0), p2.PrspFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.stLstCrmFlg")(0), p2.StLstCrmFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnAcsFlg")(0), p2.VtbOnlnAcsFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.opnFlg")(0), p3.OpnFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.zpBs144Flg")(0), p3.ZpBs144Flg)
        )
      )
    List(value)

  }
}
