package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, getMapO, mapCollect}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CardFlUaspDtoDao {

  lazy val systemSource = "cardfl"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {


    val value = for {
      mdmid <- (inMessage \ "mdmid").validate[String]
      chStLCFlg <- (inMessage \ "ChStLCFlg").validateOpt[Boolean]
      crtXzFdTpCd <- (inMessage \ "CrtXzFdTpCd").validateOpt[String]
      crtXzKkTpCd <- (inMessage \ "CrtXzKkTpCd").validateOpt[String].map(s => s.map(w => w.toInt))
      packDsc <- (inMessage \ "PackDsc").validateOpt[String]
      bnsBlncAmt <- (inMessage \ "BnsBlncAmt").validateOpt[BigDecimal]
      bnsCtgCd <- (inMessage \ "BnsCtgCd").validateOpt[String]
      bnsCtgChDt <- (inMessage \ "BnsCtgChDt").validateOpt[Long]
      cntrCd <- (inMessage \ "CntrCd").validateOpt[String]



      crdCrdt1MAmt <- (inMessage \ "CrdCrdt1MAmt").validateOpt[BigDecimal]
      crdCrdt2MAmt <- (inMessage \ "CrdCrdt2MAmt").validateOpt[BigDecimal]
      crdCrdt3MAmt <- (inMessage \ "CrdCrdt3MAmt").validateOpt[BigDecimal]
      crdDbt1MAmt <- (inMessage \ "CrdDbt1MAmt").validateOpt[BigDecimal]
      crdDbt2MAmt <- (inMessage \ "CrdDbt2MAmt").validateOpt[BigDecimal]
      crdDbt3MAmt <- (inMessage \ "CrdDbt3MAmt").validateOpt[BigDecimal]
      crdPrch1MAmt <- (inMessage \ "CrdPrch1MAmt").validateOpt[BigDecimal]
      crdPrch2MAmt <- (inMessage \ "CrdPrch2MAmt").validateOpt[BigDecimal]
      crdPrch3MAmt <- (inMessage \ "CrdPrch3MAmt").validateOpt[BigDecimal]
      crdTxn1MCnt <- (inMessage \ "CrdTxn1MCnt").validateOpt[BigDecimal]
      crdTxn2MCnt <- (inMessage \ "CrdTxn2MCnt").validateOpt[BigDecimal]
      crdTxn3MCnt <- (inMessage \ "CrdTxn3MCnt").validateOpt[BigDecimal]
      ctprtFlg <- (inMessage \ "CtprtFlg").validateOpt[Boolean]
      gndrCd <- (inMessage \ "GndrCd").validateOpt[String]


      incmCnfAmtNc <- (inMessage \ "IncmCnfAmtNc").validateOpt[BigDecimal]
      invstPrtfAmtNc <- (inMessage \ "InvstPrtfAmtNc").validateOpt[BigDecimal]
      ipFlg <- (inMessage \ "IpFlg").validateOpt[Boolean]
      pkgClsDt <- (inMessage \ "PkgClsDt").validateOpt[Long]
      pkgStrtDt <- (inMessage \ "PkgStrtDt").validateOpt[Long]
      prspFlg <- (inMessage \ "PrspFlg").validateOpt[Boolean]
      rgnCd <- (inMessage \ "RgnCd").validateOpt[String]
      rtSpnd30Amt <- (inMessage \ "RtSpnd30Amt").validateOpt[BigDecimal]
      rtSpnd30Cnt <- (inMessage \ "RtSpnd30Cnt").validateOpt[Int]
      rtSpnd60Amt <- (inMessage \ "RtSpnd60Amt").validateOpt[BigDecimal]
      rtSpnd60Cnt <- (inMessage \ "RtSpnd60Cnt").validateOpt[Int]
      rtSpnd90Amt <- (inMessage \ "RtSpnd90Amt").validateOpt[BigDecimal]
      rtSpnd90Cnt <- (inMessage \ "RtSpnd90Cnt").validateOpt[Int]
      stLstCrmFlg <- (inMessage \ "StLstCrmFlg").validateOpt[Boolean]
      ttAnnAmtNc <- (inMessage \ "TtAnnAmtNc").validateOpt[BigDecimal]
      ttBlncAmt <- (inMessage \ "TtBlncAmt").validateOpt[BigDecimal]
      vtbOnlnAcsFlg <- (inMessage \ "VtbOnlnAcsFlg").validateOpt[Boolean]
      vtbOnlnLstSsnDt <- (inMessage \ "VtbOnlnLstSsnDt").validateOpt[Long]
      opnFlg <- (inMessage \ "OpnFlg").validateOpt[Boolean]
      actCatMDMCd <- (inMessage \ "ActCatMDMCd").validateOpt[String]
      zpBs144Flg <- (inMessage \ "ZpBs144Flg").validateOpt[Boolean]
      srvcMdl <- (inMessage \ "SrvcMdl").validateOpt[String]
      zFrntClntId <- (inMessage \ "ZFrntClntId").validateOpt[Long]
      pfmManSapId <- (inMessage \ "PfmManSapId").validateOpt[String]
      intOrgGenId <- (inMessage \ "IntOrgGenId").validateOpt[String]
      zfrntChngCd <- (inMessage \ "ZfrntChngCd").validateOpt[String]
    } yield {

      UaspDto(
        id = mdmid,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Cnt")(0), rtSpnd30Cnt) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Cnt")(0), rtSpnd60Cnt) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.crtXzKkTpCd")(0), crtXzKkTpCd) ++
          getMapO[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Cnt")(0), rtSpnd90Cnt)
        ,
        dataLong = Map.empty ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.bnsCtgChDt")(0), bnsCtgChDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.pkgClsDt")(0), pkgClsDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.pkgStrtDt")(0), pkgStrtDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnLstSsnDt")(0), vtbOnlnLstSsnDt) ++
          getMapO[Long](dtoMap("app.uaspdto.fields.cardfl.zFrntClntId")(0), zFrntClntId)
        ,
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = mapCollect[BigDecimal](getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.bnsBlncAmt")(0), bnsBlncAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt1MAmt")(0), crdCrdt1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt2MAmt")(0), crdCrdt2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt3MAmt")(0), crdCrdt3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt1MAmt")(0), crdDbt1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt2MAmt")(0), crdDbt2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt3MAmt")(0), crdDbt3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch1MAmt")(0), crdPrch1MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch2MAmt")(0), crdPrch2MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch3MAmt")(0), crdPrch3MAmt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn1MCnt")(0), crdTxn1MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn2MCnt")(0), crdTxn2MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn3MCnt")(0), crdTxn3MCnt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.incmCnfAmtNc")(0), incmCnfAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.invstPrtfAmtNc")(0), invstPrtfAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Amt")(0), rtSpnd30Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Amt")(0), rtSpnd60Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Amt")(0), rtSpnd90Amt.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttAnnAmtNc")(0), ttAnnAmtNc.orNull[BigDecimal]),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttBlncAmt")(0), ttBlncAmt.orNull[BigDecimal])),
        dataString = mapCollect[String](getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.mdmid")(0), mdmid),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.crtXzFdTpCd")(0), crtXzFdTpCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.packDsc")(0), packDsc.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.system_source")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.bnsCtgCd")(0), bnsCtgCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.cntrCd")(0), cntrCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.gndrCd")(0), gndrCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.rgnCd")(0), rgnCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.actCatMDMCd")(0), actCatMDMCd.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.srvcMdl")(0), srvcMdl.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.pfmManSapId")(0), pfmManSapId.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.intOrgGenId")(0), intOrgGenId.orNull[String]),
          getMapEntry[String](dtoMap("app.uaspdto.fields.cardfl.zfrntChngCd")(0), zfrntChngCd.orNull[String])
        ),
        dataBoolean = Map.empty ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.chStLCFlg")(0), chStLCFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.ctprtFlg")(0), ctprtFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.ipFlg")(0), ipFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.prspFlg")(0), prspFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.stLstCrmFlg")(0), stLstCrmFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnAcsFlg")(0), vtbOnlnAcsFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.opnFlg")(0), opnFlg) ++
          getMapO[Boolean](dtoMap("app.uaspdto.fields.cardfl.zpBs144Flg")(0), zpBs144Flg)

      )
    }
    List(value)


  }


}
