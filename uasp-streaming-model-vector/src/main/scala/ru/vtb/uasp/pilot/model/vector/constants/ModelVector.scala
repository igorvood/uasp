package ru.vtb.uasp.pilot.model.vector.constants

import com.typesafe.config.{Config, ConfigFactory}

object ModelVector {
  val config: Config = ConfigFactory.load("mv_application.conf")
  val fields: Config = config.getConfig("fields")
  val vectors: Config = config.getConfig("vectors")
  val fieldsMetaData: Config = config.getConfig("field_metadata_fields")
  val valueTypes: Config = config.getConfig("types")
  val case_classification: Config = config.getConfig("case_classification")

  val CLASSIFICATION: String = fields.getString("CLASSIFICATION")

  val FULL_VECTOR: String = vectors.getString("FULL_VECTOR")

  val TYPE: String = fieldsMetaData.getString("TYPE")
  val DTTM_FORMAT: String = fieldsMetaData.getString("DTTM_FORMAT")
  val W4_FIELD_NAME: String = fieldsMetaData.getString("W4_FIELD_NAME")
  val CA_FIELD_NAME: String = fieldsMetaData.getString("CA_FIELD_NAME")
  val CFT_FIELD_NAME: String = fieldsMetaData.getString("CFT_FIELD_NAME")
  val PROFILE_FIELD_NAME: String = fieldsMetaData.getString("PROFILE_FIELD_NAME")
  val WITHDRAW_FIELD_NAME: String = fieldsMetaData.getString("WITHDRAW_FIELD_NAME")

  val STRING: String = valueTypes.getString("STRING")
  val INT: String = valueTypes.getString("INT")
  val LONG: String = valueTypes.getString("LONG")
  val DOUBLE: String = valueTypes.getString("DOUBLE")
  val FLOAT: String = valueTypes.getString("FLOAT")
  val BOOLEAN: String = valueTypes.getString("BOOLEAN")
  val BIG_DECIMAL: String = valueTypes.getString("BIG_DECIMAL")

  val CASE_8: String = case_classification.getString("CASE_8")
  val CASE_29: String = case_classification.getString("CASE_29")
  val CASE_38: String = case_classification.getString("CASE_38")
  val CASE_39: String = case_classification.getString("CASE_39")
  val CASE_39_NEW: String = case_classification.getString("CASE_39_NEW")
  val CASE_44: String = case_classification.getString("CASE_44")
  val CASE_56: String = case_classification.getString("CASE_56")
  val CASE_57: String = case_classification.getString("CASE_57")

  val CASE_71: String = case_classification.getString("CASE_71")
  val CASE_51: String = case_classification.getString("CASE_51")
  val CASE_48: String = case_classification.getString("CASE_48")
  val CASE_68: String = case_classification.getString("CASE_68")
}
