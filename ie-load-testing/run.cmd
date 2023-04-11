rem export sslTruststoreLocation="C:\Work\uasp-streaming-filter-itest\src\test\resources\kafka-trust.pfx"
rem export sslKeystoreLocation="C:\Work\uasp-streaming-filter-itest\src\test\resources\APD00.13.01-USBP-kafka-cluster-uasp.pfx"


set sslTruststoreLocation=C:\Work\uasp-streaming-filter-itest\src\test\resources\kafka-trust.pfx
set sslKeystoreLocation=C:\Work\uasp-streaming-filter-itest\src\test\resources\APD00.13.01-USBP-kafka-cluster-uasp.pfx
set sslTruststorePassword=kafkauasppassword
set sslKeystorePassword=kafkauasppassword
set sslKeyPassword=kafkauasppassword

java -Xmx10g -classpath target/ie-load-testing-1.0-SNAPSHOT.jar;target/ie-load-testing-1.0-SNAPSHOT-tests.jar;original-ie-load-testing-1.0-SNAPSHOT.jar io.gatling.app.Gatling -s ru.vtb.ie.test.UaspTarantoolScript > o.log


