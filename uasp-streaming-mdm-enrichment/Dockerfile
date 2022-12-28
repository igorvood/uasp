#FROM maven:3.8-amazoncorretto-8 as builder
#COPY . .
#RUN mvn install

#FROM flink:1.16.0-java11
FROM flink:1.12.1-java8

RUN echo "metrics.reporters: prom" >> "$FLINK_HOME/conf/flink-conf.yaml"; \
    echo "metrics.reporter.prom.factory.class: org.apache.flink.metrics.prometheus.PrometheusReporterFactory" >> "$FLINK_HOME/conf/flink-conf.yaml" \
#MOUNT ./target/uasp-streaming-mdm-enrichment-*.jar $FLINK_HOME/usrlib/
#COPY --from=builder /home/gradle/build/libs/*.jar $FLINK_HOME/usrlib/
RUN mkdir /state && chown flink:flink /state  # workaround for https://github.com/docker/compose/issues/3270
