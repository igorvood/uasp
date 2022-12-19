package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum.InputFormatEnum

trait FormatSwitcher {

  val inputDataFormat: InputFormatEnum

}
