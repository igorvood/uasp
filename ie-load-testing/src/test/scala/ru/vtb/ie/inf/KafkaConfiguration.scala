package ru.vtb.ie.inf

trait KafkaConfiguration[CFG_DTO] {
  def getKafkaCfg(sysEnv: Map[String, String]): CFG_DTO

  val sysEnv: Map[String, String] = sys.env
  val config: CFG_DTO = getKafkaCfg(sysEnv)

}
