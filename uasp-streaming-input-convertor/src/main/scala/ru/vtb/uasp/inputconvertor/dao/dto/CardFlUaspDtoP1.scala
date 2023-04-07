package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

/* пришлось разбить на несколько ДТО тк идет ограничение по колву полей, максисмум 22. Это ограничение тупла*/
case class CardFlUaspDtoP1(
                            mdmid: String,
                            ChStLCFlg: Option[Boolean],
                            CrtXzFdTpCd: Option[String],
                            private val CrtXzKkTpCd: Option[String],
                            PackDsc: Option[String],
                            BnsBlncAmt: Option[BigDecimal],
                            BnsCtgCd: Option[String],
                            BnsCtgChDt: Option[Long],
                            CntrCd: Option[String],
                            CrdCrdt1MAmt: Option[BigDecimal],
                            CrdCrdt2MAmt: Option[BigDecimal],
                            CrdCrdt3MAmt: Option[BigDecimal],
                            CrdDbt1MAmt: Option[BigDecimal],
                            CrdDbt2MAmt: Option[BigDecimal],
                            CrdDbt3MAmt: Option[BigDecimal],
                            CrdPrch1MAmt: Option[BigDecimal],
                            CrdPrch2MAmt: Option[BigDecimal],
                            CrdPrch3MAmt: Option[BigDecimal],
                            CrdTxn1MCnt: Option[BigDecimal],
                            CrdTxn2MCnt: Option[BigDecimal],
                          ) {

  val CrtXzKkTpCdInt: Option[Int] = CrtXzKkTpCd.map(w => w.toInt)
}

case class CardFlUaspDtoP2(
                            CrdTxn3MCnt: Option[BigDecimal],
                            CtprtFlg: Option[Boolean],
                            GndrCd: Option[String],
                            IncmCnfAmtNc: Option[BigDecimal],
                            InvstPrtfAmtNc: Option[BigDecimal],
                            IpFlg: Option[Boolean],
                            PkgClsDt: Option[Long],
                            PkgStrtDt: Option[Long],
                            PrspFlg: Option[Boolean],
                            RgnCd: Option[String],
                            RtSpnd30Amt: Option[BigDecimal],
                            RtSpnd30Cnt: Option[Int],
                            RtSpnd60Amt: Option[BigDecimal],
                            RtSpnd60Cnt: Option[Int],
                            RtSpnd90Amt: Option[BigDecimal],
                            RtSpnd90Cnt: Option[Int],
                            StLstCrmFlg: Option[Boolean],
                            TtAnnAmtNc: Option[BigDecimal],
                            TtBlncAmt: Option[BigDecimal],
                            VtbOnlnAcsFlg: Option[Boolean],
                            VtbOnlnLstSsnDt: Option[Long],
                          )

case class CardFlUaspDtoP3(
                            OpnFlg: Option[Boolean],
                            ActCatMDMCd: Option[String],
                            ZpBs144Flg: Option[Boolean],
                            SrvcMdl: Option[String],
                            ZFrntClntId: Option[Long],
                            PfmManSapId: Option[String],
                            IntOrgGenId: Option[String],
                            ZfrntChngCd: Option[String],
                          )

object CardFlUaspDtoP1 {
  implicit val reads: Reads[CardFlUaspDtoP1] = Json.reads[CardFlUaspDtoP1]
  implicit val writes: OWrites[CardFlUaspDtoP1] = Json.writes[CardFlUaspDtoP1]
}

object CardFlUaspDtoP2 {
  implicit val reads: Reads[CardFlUaspDtoP2] = Json.reads[CardFlUaspDtoP2]
  implicit val writes: OWrites[CardFlUaspDtoP2] = Json.writes[CardFlUaspDtoP2]
}

object CardFlUaspDtoP3 {
  implicit val reads: Reads[CardFlUaspDtoP3] = Json.reads[CardFlUaspDtoP3]
  implicit val writes: OWrites[CardFlUaspDtoP3] = Json.writes[CardFlUaspDtoP3]
}