package ru.vtb.uasp.inputconvertor.utils.hash

import org.apache.commons.codec.digest.DigestUtils

import java.security.{MessageDigest, NoSuchAlgorithmException}
import scala.collection.mutable

object HashUtils {
  def getHashSHA256PrependingSalt(string: String, salt: String): String = {
    var generatedStringSha256: String = null
    val saltByte = salt.getBytes
    try {
      val md: MessageDigest = MessageDigest.getInstance("SHA-256")
      md.update(saltByte)
      val bytes: Array[Byte] = md.digest(string.getBytes)
      val sb: mutable.StringBuilder = new mutable.StringBuilder
      for (i <- 0 until bytes.length) {
        sb.append(Integer.toString((bytes(i) & 0xff) + 0x100, 16).substring(1))
      }
      generatedStringSha256 = sb.toString
    } catch {
      case e: NoSuchAlgorithmException =>
        e.printStackTrace()
    }
    generatedStringSha256

  }

  def getHashSHA256AppendingSalt(string: String, salt: String): String = {
    DigestUtils.sha256Hex(string + salt)
  }

  def getMD5Hash(string: String): String = {
    DigestUtils.md5Hex(string)
  }

}
