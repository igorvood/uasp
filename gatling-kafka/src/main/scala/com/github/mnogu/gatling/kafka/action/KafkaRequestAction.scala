package com.github.mnogu.gatling.kafka.action

import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.session._
import io.gatling.commons.util.DefaultClock
import io.gatling.commons.validation.Validation
import io.gatling.core.CoreComponents
import io.gatling.core.util.NameGen
import org.apache.kafka.clients.producer._
import io.gatling.commons.util.Throwables._

import scala.util.control.NonFatal

class KafkaRequestAction[K, V](val producer: KafkaProducer[K, V],
                               val kafkaAttributes: KafkaAttributes[K, V],
                               val coreComponents: CoreComponents,
                               val kafkaProtocol: KafkaProtocol,
                               val throttled: Boolean,
                               val next: Action,
                               val callback: Option[(Session, Option[Throwable]) => Unit])
  extends ExitableAction with NameGen {

  private val callbackFunc = callback.getOrElse {
    (_: Session, ex: Option[Throwable]) =>
      if (ex.isDefined) {
        ex.get.printStackTrace()
        System.exit(-1)
      }
  }

  val statsEngine = coreComponents.statsEngine
  val clock = new DefaultClock
  override val name = genName("kafkaRequest")

  override def execute(session: Session): Unit = recover(session) {
    kafkaAttributes.requestName(session).flatMap { requestName =>
      val outcome =
        try {
          sendRequest(requestName, producer, kafkaAttributes, throttled, session)
        } catch {
          case NonFatal(e) =>
            statsEngine.reportUnbuildableRequest(session.scenario, session.groups, requestName, e.detailedMessage)
            callbackFunc(session, Some(e))
            throw e
        }
      outcome.onSuccess( _ => callbackFunc(session, None))
      outcome.onFailure {
        errorMessage =>
          statsEngine.reportUnbuildableRequest(
            session.scenario,
            session.groups,
            requestName,
            errorMessage)
          callbackFunc(session, Some(new Throwable(errorMessage)))
      }
      outcome
    }
  }

  private def sendRequest(requestName: String,
                          producer: Producer[K, V],
                          kafkaAttributes: KafkaAttributes[K, V],
                          throttled: Boolean,
                          session: Session): Validation[Unit] =
    kafkaAttributes payload session map { payload =>
      val record = kafkaAttributes.key match {
        case Some(k) =>
          new ProducerRecord[K, V](kafkaProtocol.topic, k(session).toOption.get, payload)
        case None =>
          new ProducerRecord[K, V](kafkaProtocol.topic, payload)
      }

      val requestStartDate = clock.nowMillis

      producer.send(record, new Callback() {

        override def onCompletion(m: RecordMetadata, e: Exception): Unit = {
          if (e != null) throw e

          val requestEndDate = clock.nowMillis
          statsEngine.logResponse(
            session.scenario,
            session.groups,
            requestName,
            startTimestamp = requestStartDate,
            endTimestamp = requestEndDate,
            if (e == null) OK else KO,
            None,
            if (e == null) None else Some(e.getMessage)
          )

          if (throttled) {
            coreComponents.throttler.foreach(_.throttle(session.scenario, () => next ! session))
            //coreComponents.throttler.throttle(session.scenario, () => next ! session)
          } else {
            next ! session
          }
        }
        }
      )
    }

}