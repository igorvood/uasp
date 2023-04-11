package ru.vtb.ie.generate.json.datamodel

import ru.vtb.ie.generate.json.abstraction.AbstractStringIdentyfyedEntity
import ru.vtb.ie.generate.json.dsl.Predef.PropAssoc

import scala.language.postfixOps

case class BigJsonMeta(name: String) extends AbstractStringIdentyfyedEntity {
  override def entityName: String = name

  override def fields = Set(
    "customer_id" asStr { (id, _) => id },
    "uuid" asStr { (_, _) => java.util.UUID.randomUUID().toString },


    "max_duration_last_day_2_2" asNull,
//    "cstmr_prime_flg" asNull,
    "cnt_days_from_last_operation_5_2" asConst 1645444249501d,
    "cstmr_privilege_flg" asConst "0.0",
    "last_return_code_2_10" asConst "2.0",
    "cnt_days_from_last_operation_6_3_prc" asConst 1644130911501d,
    "cnt_days_from_last_call_9_1" asNull, // asConst 364860807558682919d,
    "cnt_calls_last_3_days_8_1" asConst 0,
    "cnt_days_from_last_call_11_1" asNull, //  asConst -1212772316724445978d,
//    "cnt_days_from_last_operation_2_5" asNull,
    "cnt_days_from_last_call_2_1" asNull, //asConst 244460982983914885d,
    "cnt_days_from_last_operation_6_2" asConst 1553575061501d,
    "cstmr_level2_segment_nm" asConst "ИНЕРТНЫЕ ВКЛАДЧИКИ",
    "cstmr_consent_bki_end_dt_diff_days" asConst 1596890905501d,
    "cstmr_consent_bki_flg" asConst "0.0",
    "ratio_6_3_prc_opt" asConst 1.538214d,
    "sum_operations_last_week_6_2" asConst 0,
    "cnt_days_from_last_operation_6_3_cr" asConst 1642448107501d,
    "cstmr_places_work_3_years_cnt" asNull, //asConst -948906996d,
    "max_duration_last_3_days_2_2" asNull, //asConst 0.2629904181197088d,
    "cnt_days_from_last_call_8_1" asNull, //asConst 4322310811258762269d,
    "time_to_max_repayment_9_2" asNull, //asConst -3014900128589639906d,
    "cnt_days_from_last_operation_2_4" asConst 1645188505501d,
    "k_65_11_1" asNull, //asConst 7175729820733919739d,
    "time_from_last_operation_5_3" asConst 1645220279501d,
    "sum_operations_last_week_2_10" asConst 500,
//    "cstmr_tax_resident_flg" asNull,

    "sum_operations_last_quarter_2_2" asConst 135543.09d,
    "sum_operations_last_quarter_6_2" asConst 0,
    "cnt_days_to_close_plan_dt_7_3" asNull, // asConst 4560341076013871290d,
    "time_from_last_operation_11_7" asNull, //asConst -4957167509165351338d,
    "cnt_calls_last_week_8_1" asConst 0,
//    "cnt_days_from_last_unpaid_comission_2_5" asNull,
    "sum_operations_last_week_5_2" asConst 11249.83d,
    "cnt_calls_last_week_9_1" asConst 0,
    "time_from_last_operation_9_2" asNull, //asConst 7031987904165569708d,
    "cnt_days_from_last_operation_2_2" asConst 1645189442000d,
    "last_operation_sum_5_3" asConst 923.08d,
    "sum_operations_last_year_2_2" asConst 374703.59,
//    "cstmr_consent_pd_end_dt_diff_days" asNull,
    "cnt_calls_last_3_days_9_1" asConst 0,
    "cnt_days_from_last_call_6_1" asNull, //asConst -5280332173940831532d,
    "k_77_8_2" asNull, //asConst 7430824925364597395d,
    "cstmr_rkk_empl_tpn_actual_flg" asConst "1.0",
    "cstmr_package_type_cd" asConst "MULTICARTA",
    "time_from_last_operation_11_5" asNull, //asConst 824333093000730668d,
    "max_duration_last_day_2_3" asNull,
    "max_sa_period_6_3_cr" asConst 369.34078703703705d,
    "cstmr_age" asConst -438478768d,
    "cnt_days_from_last_penalty_11_10" asNull, //asConst 5440844114078193076d,
    "cnt_operations_last_month_6_2" asConst 0,
    "cnt_days_from_last_operation_2_10" asConst 1148129305501d,
    "cnt_operations_last_year_8_2" asNull, //asConst 1872509702d,
    "time_from_last_operation_8_2" asNull, //asConst -8431801418170718850d,
    "process_timestamp" asConst 1645447712874d,
    "cnt_days_from_last_oper_11_9" asNull, //asConst -8540721947745717781d,
    "days_from_open_agreem_11_2" asNull, //asConst 3402077181130889806d,
    "cnt_days_from_last_operation_7_2" asNull, //asConst -5476614852876919239d,
    "cnt_days_from_last_call_3_1" asNull, //asConst -6725977559850403722d
  )
}
