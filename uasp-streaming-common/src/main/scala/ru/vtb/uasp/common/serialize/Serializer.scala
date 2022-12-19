package ru.vtb.uasp.common.serialize

import java.io._

object Serializer {

  def serialize[T <: Serializable](obj: T): Array[Byte] = {
    var byteOut: ByteArrayOutputStream = null
    var objOut: ObjectOutputStream = null

    try {
      byteOut = new ByteArrayOutputStream()
      objOut = new ObjectOutputStream(byteOut)
      objOut.writeObject(obj)
      byteOut.toByteArray
    } finally {
      if (objOut != null)
        objOut.close()

      if (byteOut != null)
        byteOut.close()
    }
  }

  def deserialize[T <: Serializable](bytes: Array[Byte]): T = {
    var byteIn: ByteArrayInputStream = null
    var objIn: ObjectInputStream = null

    try {
      byteIn = new ByteArrayInputStream(bytes)
      objIn = new ObjectInputStream(byteIn)
      val obj = objIn.readObject().asInstanceOf[T]
      byteIn.close()
      objIn.close()
      obj
    } finally {
      if (objIn != null)
        objIn.close()

      if (byteIn != null)
        byteIn.close()
    }
  }
}
