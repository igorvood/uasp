package ru.vtb.uasp.inputconvertor.service

import com.sksamuel.avro4s.{AvroOutputStream, ScalePrecision}
import org.apache.avro.{Conversions, LogicalTypes}
import org.apache.flink.api.common.functions.RichMapFunction
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.common.constants.BigDecimalConst
import ru.vtb.uasp.common.dto.UaspDto

import java.io.ByteArrayOutputStream
import java.math.RoundingMode
import java.nio.ByteBuffer

class ConvertOutMapService extends RichMapFunction [UaspDto, (Array[Byte],Array[Byte])] {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  implicit val sp: ScalePrecision = ScalePrecision(BigDecimalConst.SCALE, BigDecimalConst.PRECISION)

  def serialize(data: UaspDto): (Array[Byte],Array[Byte]) = {
    var baos: ByteArrayOutputStream = null
    var output: AvroOutputStream[UaspDto] = null
    try {
      baos = new ByteArrayOutputStream()
      val avroOutputStream = AvroOutputStream.binary[UaspDto]
      output = avroOutputStream.to(baos).build()
      output.write(data)
    } finally {
      if (output != null) {
        output.close()
      }

      if (baos != null) {
        baos.close()
      }
    }

    (data.id.getBytes(), baos.toByteArray)
  }

  override def map(data: UaspDto): (Array[Byte],Array[Byte]) = {
    try {
      val s = serialize(data)
      s
    } catch {
      case e: Throwable => e match {
        case _ =>
          e.printStackTrace()
          throw new RuntimeException(e)
      }
    }
  }

  def bigDecimalToLogicalTypeByteBuffer(scalaBigDecimal: scala.BigDecimal): java.nio.ByteBuffer = {
    val bigDecimal = scalaBigDecimal.bigDecimal.setScale(BigDecimalConst.SCALE, RoundingMode.HALF_UP)
    logger.debug("bigDecimal: " + bigDecimal)
    val newBigDecimal =  new Conversions.DecimalConversion().toBytes(bigDecimal, null,
      LogicalTypes.decimal(BigDecimalConst.PRECISION, BigDecimalConst.SCALE))
    logger.debug("newBigDecimal: " + newBigDecimal)

    newBigDecimal
  }

  def convertBigDecimal(value: scala.BigDecimal)
  : java.nio.ByteBuffer = {
    ByteBuffer.wrap(value.underlying.unscaledValue.toByteArray)
  }

  def convertBigDecimalToBytes(mapBigDecimal: Map[String, BigDecimal]): java.util.Map[String, java.nio.ByteBuffer]  = {
    if (mapBigDecimal == null) Map[String, BigDecimal]()

    val convertMap: java.util.Map[String, java.nio.ByteBuffer] = new java.util.HashMap[String, java.nio.ByteBuffer]
    mapBigDecimal.foreach(a => convertMap.put(a._1, bigDecimalToLogicalTypeByteBuffer(a._2.bigDecimal)))

    logger.debug("convertMap size: " + convertMap.size)

    convertMap
  }

}
