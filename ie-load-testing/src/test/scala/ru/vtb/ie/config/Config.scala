package ru.vtb.ie.config

case class Config(
                   userCount: Int,
                   countSeconds: Int,
                   baseUrlIE: String,
                   baseUrlTarantool: String,
                   topicSmallJson: String,
                   topicBigJson: String,
                   bootstrapServers: String,
                   sslTruststoreLocation: String,
                   sslTruststorePassword: String,
                   sslKeystoreLocation: String,
                   sslKeystorePassword: String,
                   sslKeyPassword: String,
                   isPrintRqBody: Boolean,
                   isPrintRspTarantoolBody: Boolean,
                   isPrintRspIEBody: Boolean,
                   checkTarantoolRest: Boolean,
                   isPrintSession: Boolean,
                 ) {
  override def toString: String = {
    "Config{" + "\n" +
      "isPrintSession: " + isPrintSession + "\n" +
      "isPrintRqBody: " + isPrintRqBody + "\n" +
      "isPrintRspTarantoolBody: " + isPrintRspTarantoolBody + "\n" +
      "isPrintRspIEBody: " + isPrintRspIEBody + "\n" +
      "checkTarantoolRest: " + checkTarantoolRest + "\n" +
      "userCount: " + userCount + "\n" +
      "countSeconds: " + countSeconds + "\n" +
      "baseUrlIE: " + baseUrlIE + "\n" +
      "baseUrlTarantool: " + baseUrlTarantool + "\n" +
      "isPrintRspBody: " + isPrintRspTarantoolBody + "\n" +
      "topicSmallJson: " + topicSmallJson + "\n" +
      "topicBigJson: " + topicBigJson + "\n" +
      "bootstrapServers: " + bootstrapServers + "\n" +
      "sslTruststoreLocation: " + sslTruststoreLocation + "\n" +
      "sslTruststorePassword: " + sslTruststorePassword + "\n" +
      "sslKeystoreLocation: " + sslKeystoreLocation + "\n" +
      "sslKeystorePassword: " + sslKeystorePassword + "\n" +
      "sslKeyPassword: " + sslKeyPassword + "\n" + "}"
  }
}