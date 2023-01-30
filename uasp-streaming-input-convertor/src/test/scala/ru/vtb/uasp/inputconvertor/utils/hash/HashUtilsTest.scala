package ru.vtb.uasp.inputconvertor.utils.hash

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class HashUtilsTest extends AnyFlatSpec with should.Matchers {

  "Result with prepending salt" should "be hash sha256" in {
    val inputString = "Test"
    val salt = "testSalt"
    val expectedHashSHA256 = "62266c249c72c830f66e521bf8b48d221d7cdda7b2f87ab3a7e6cd79c66e82c3"

    val result = HashUtils.getHashSHA256PrependingSalt(inputString, salt)

    result shouldEqual expectedHashSHA256

  }

  "Result appending salt" should " be hash sha256" in {
    val inputString = "Test"
    val salt = "testSalt"
    val expectedHashSHA256 = "fcd21e84fee15d9ca0646d82c01cfb5ff324ba4a7c04493e9fdf78b06f33d030"

    val result = HashUtils.getHashSHA256AppendingSalt(inputString, salt)

    result shouldEqual expectedHashSHA256
  }

  "Result" should " be hash MD5" in {
    val inputString = "Test"
    val expectedHashMD5 = "0cbc6611f5540bd0809a388dc95a615b"

    val result = HashUtils.getMD5Hash(inputString)

    result shouldEqual expectedHashMD5
  }
}
