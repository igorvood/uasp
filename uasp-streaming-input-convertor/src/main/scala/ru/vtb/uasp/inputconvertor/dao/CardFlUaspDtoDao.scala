package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CardFlUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "cardfl"

    lazy val mdmid: String = (inMessage \ "mdmid").extract[String]
    lazy val chStLCFlg: Boolean = (inMessage \ "ChStLCFlg").extract[Boolean]
    lazy val crtXzFdTpCd: String = (inMessage \ "CrtXzFdTpCd").extract[String]
    lazy val crtXzKkTpCd: String = (inMessage \ "CrtXzKkTpCd").extract[String]
    lazy val packDsc: String = (inMessage \ "PackDsc").extract[String]
    lazy val bnsBlncAmt: BigDecimal = (inMessage \ "BnsBlncAmt").extract[BigDecimal]
    lazy val bnsCtgCd: String = (inMessage \ "BnsCtgCd").extract[String]
    lazy val bnsCtgChDt = (inMessage \ "BnsCtgChDt").extract[Long]
    lazy val cntrCd: String = (inMessage \ "CntrCd").extract[String]
    lazy val crdCrdt1MAmt: BigDecimal = (inMessage \ "CrdCrdt1MAmt").extract[BigDecimal]
    lazy val crdCrdt2MAmt: BigDecimal = (inMessage \ "CrdCrdt2MAmt").extract[BigDecimal]
    lazy val crdCrdt3MAmt: BigDecimal = (inMessage \ "CrdCrdt3MAmt").extract[BigDecimal]
    lazy val crdDbt1MAmt: BigDecimal = (inMessage \ "CrdDbt1MAmt").extract[BigDecimal]
    lazy val crdDbt2MAmt: BigDecimal = (inMessage \ "CrdDbt2MAmt").extract[BigDecimal]
    lazy val crdDbt3MAmt: BigDecimal = (inMessage \ "CrdDbt3MAmt").extract[BigDecimal]
    lazy val crdPrch1MAmt: BigDecimal = (inMessage \ "CrdPrch1MAmt").extract[BigDecimal]
    lazy val crdPrch2MAmt: BigDecimal = (inMessage \ "CrdPrch2MAmt").extract[BigDecimal]
    lazy val crdPrch3MAmt: BigDecimal = (inMessage \ "CrdPrch3MAmt").extract[BigDecimal]
    lazy val crdTxn1MCnt: BigDecimal = (inMessage \ "CrdTxn1MCnt").extract[BigDecimal]
    lazy val crdTxn2MCnt: BigDecimal = (inMessage \ "CrdTxn2MCnt").extract[BigDecimal]
    lazy val crdTxn3MCnt: BigDecimal = (inMessage \ "CrdTxn3MCnt").extract[BigDecimal]
    lazy val ctprtFlg: Boolean = (inMessage \ "CtprtFlg").extract[Boolean]
    lazy val gndrCd: String = (inMessage \ "GndrCd").extract[String]
    lazy val incmCnfAmtNc: BigDecimal = (inMessage \ "IncmCnfAmtNc").extract[BigDecimal]
    lazy val invstPrtfAmtNc: BigDecimal = (inMessage \ "InvstPrtfAmtNc").extract[BigDecimal]
    lazy val ipFlg: Boolean = (inMessage \ "IpFlg").extract[Boolean]
    lazy val pkgClsDt = (inMessage \ "PkgClsDt").extract[Long]
    lazy val pkgStrtDt = (inMessage \ "PkgStrtDt").extract[Long]
    lazy val prspFlg: Boolean = (inMessage \ "PrspFlg").extract[Boolean]
    lazy val rgnCd: String = (inMessage \ "RgnCd").extract[String]
    lazy val rtSpnd30Amt: BigDecimal = (inMessage \ "RtSpnd30Amt").extract[BigDecimal]
    lazy val rtSpnd30Cnt = (inMessage \ "RtSpnd30Cnt").extract[Int]
    lazy val rtSpnd60Amt: BigDecimal = (inMessage \ "RtSpnd60Amt").extract[BigDecimal]
    lazy val rtSpnd60Cnt = (inMessage \ "RtSpnd60Cnt").extract[Int]
    lazy val rtSpnd90Amt: BigDecimal = (inMessage \ "RtSpnd90Amt").extract[BigDecimal]
    lazy val rtSpnd90Cnt = (inMessage \ "RtSpnd90Cnt").extract[Int]
    lazy val stLstCrmFlg: Boolean = (inMessage \ "StLstCrmFlg").extract[Boolean]
    lazy val ttAnnAmtNc: BigDecimal = (inMessage \ "TtAnnAmtNc").extract[BigDecimal]
    lazy val ttBlncAmt: BigDecimal = (inMessage \ "TtBlncAmt").extract[BigDecimal]
    lazy val vtbOnlnAcsFlg: Boolean = (inMessage \ "VtbOnlnAcsFlg").extract[Boolean]
    lazy val vtbOnlnLstSsnDt = (inMessage \ "VtbOnlnLstSsnDt").extract[Long]
    lazy val opnFlg: Boolean = (inMessage \ "OpnFlg").extract[Boolean]
    lazy val actCatMDMCd = (inMessage \ "ActCatMDMCd").extract[String]
    lazy val zpBs144Flg: Boolean = (inMessage \ "ZpBs144Flg").extract[Boolean]
    lazy val srvcMdl = (inMessage \ "SrvcMdl").extract[String]
    lazy val zFrntClntId = (inMessage \ "ZFrntClntId").extract[Long]
    lazy val pfmManSapId = (inMessage \ "PfmManSapId").extract[String]
    lazy val intOrgGenId = (inMessage \ "IntOrgGenId").extract[String]
    lazy val zfrntChngCd = (inMessage \ "ZfrntChngCd").extract[String]

    val dataInt = Map[String, Int]() ++
      getMap[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Cnt")(0), rtSpnd30Cnt) ++
      getMap[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Cnt")(0), rtSpnd60Cnt) ++
      getMap[Int](dtoMap("app.uaspdto.fields.cardfl.crtXzKkTpCd")(0), crtXzKkTpCd.toInt) ++
      getMap[Int](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Cnt")(0), rtSpnd90Cnt)

    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.cardfl.bnsCtgChDt")(0), bnsCtgChDt) ++
      getMap[Long](dtoMap("app.uaspdto.fields.cardfl.pkgClsDt")(0), pkgClsDt) ++
      getMap[Long](dtoMap("app.uaspdto.fields.cardfl.pkgStrtDt")(0), pkgStrtDt) ++
      getMap[Long](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnLstSsnDt")(0), vtbOnlnLstSsnDt) ++
      getMap[Long](dtoMap("app.uaspdto.fields.cardfl.zFrntClntId")(0), zFrntClntId)


    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.bnsBlncAmt")(0), bnsBlncAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt1MAmt")(0), crdCrdt1MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt2MAmt")(0), crdCrdt2MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdCrdt3MAmt")(0), crdCrdt3MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt1MAmt")(0), crdDbt1MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt2MAmt")(0), crdDbt2MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdDbt3MAmt")(0), crdDbt3MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch1MAmt")(0), crdPrch1MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch2MAmt")(0), crdPrch2MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdPrch3MAmt")(0), crdPrch3MAmt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn1MCnt")(0), crdTxn1MCnt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn2MCnt")(0), crdTxn2MCnt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.crdTxn3MCnt")(0), crdTxn3MCnt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.incmCnfAmtNc")(0), incmCnfAmtNc) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.invstPrtfAmtNc")(0), invstPrtfAmtNc) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd30Amt")(0), rtSpnd30Amt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd60Amt")(0), rtSpnd60Amt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.rtSpnd90Amt")(0), rtSpnd90Amt) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttAnnAmtNc")(0), ttAnnAmtNc) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.cardfl.ttBlncAmt")(0), ttBlncAmt)

    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.mdmid")(0), mdmid) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.crtXzFdTpCd")(0), crtXzFdTpCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.packDsc")(0), packDsc) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.system_source")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.bnsCtgCd")(0), bnsCtgCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.cntrCd")(0), cntrCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.gndrCd")(0), gndrCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.rgnCd")(0), rgnCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.actCatMDMCd")(0), actCatMDMCd) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.srvcMdl")(0), srvcMdl) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.pfmManSapId")(0), pfmManSapId) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.intOrgGenId")(0), intOrgGenId) ++
      getMap[String](dtoMap("app.uaspdto.fields.cardfl.zfrntChngCd")(0), zfrntChngCd)

    val dataBoolean = Map[String, Boolean]() ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.chStLCFlg")(0), chStLCFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.ctprtFlg")(0), ctprtFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.ipFlg")(0), ipFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.prspFlg")(0), prspFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.stLstCrmFlg")(0), stLstCrmFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.vtbOnlnAcsFlg")(0), vtbOnlnAcsFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.opnFlg")(0), opnFlg) ++
      getMap[Boolean](dtoMap("app.uaspdto.fields.cardfl.zpBs144Flg")(0), zpBs144Flg)


    UaspDto(
      id = mdmid,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = dataInt,
      dataLong = dataLong,
      dataFloat = dataFloat,
      dataDouble = dataDouble,
      dataDecimal = dataDecimal,
      dataString = dataString,
      dataBoolean = dataBoolean
    )

  }


}
