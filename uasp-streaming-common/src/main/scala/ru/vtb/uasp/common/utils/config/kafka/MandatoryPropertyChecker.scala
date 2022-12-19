package ru.vtb.uasp.common.utils.config.kafka

import java.util.Properties

trait MandatoryPropertyChecker {

  def requiredProperty: Set[String]

  def nullProperties(property: Properties): String = requiredProperty.map(prop => prop -> property.getProperty(prop)).filter(p => p._2 == null).map(_._1).mkString(",")

  def nullProperties(prpMap: Map[String,String]): String = requiredProperty.map(prop => prop -> prpMap.get(prop)).filter(p => p._2.isEmpty).map(_._1).mkString(",")


}
