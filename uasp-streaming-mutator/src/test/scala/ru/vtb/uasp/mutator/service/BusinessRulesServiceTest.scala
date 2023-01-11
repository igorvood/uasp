package ru.vtb.uasp.mutator.service

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.BusinessRulesServiceTest.{dtoNoFields, genUaspOperation}
import ru.vtb.uasp.mutator.service.dto._

import java.util.Calendar
import scala.collection.Set


class BusinessRulesServiceTest extends AnyFlatSpec {
  behavior of "Mutate Operation"

  it should "success" in {
    val cnt = 10
    val dtoAdd = new BusinessRulesService(_ => {
      genUaspOperation(cnt, Set(Add()))
    }).processWithDlq(dtoNoFields).right.get
    assertResult(cnt * 7)(countVals(dtoAdd))

    val dtoMutate = new BusinessRulesService(_ => {
      genUaspOperation(cnt, Set(Mutate()))
    })
      .processWithDlq(dtoAdd).right.get
    assertResult(cnt * 7)(countVals(dtoMutate))

    val dtoDelete = new BusinessRulesService(_ => {
      genUaspOperation(cnt, Set(Delete()))
    })
      .processWithDlq(dtoMutate).right.get
    assertResult(0)(countVals(dtoDelete))

  }

  it should "error on adds null value" in {
    val service = new BusinessRulesService(_ => {
      Set[UaspOperation](UaspOperation("n", StringMap(null), "asd", Add()))
    })

    val dto = service.processWithDlq(dtoNoFields).left.get
    val errUasp = JsonConvertInService.deserialize[UaspDto](dto.value).right.get
    errUasp.dataString.get(errFieldName)
      .map(d=>assertResult("requirement failed: RuleName 'n': Uncompatitible value 'null' for operation Add() ")(d))
      .getOrElse(throw new IllegalStateException("not found field "+errFieldName+ " in dataString mal"))
  }

  it should "error on Mutate null value" in {
    val service = new BusinessRulesService(_ => {
      Set[UaspOperation](UaspOperation("n", StringMap(null), "asd", Mutate()))
    })

    val dto = service.processWithDlq(dtoNoFields).left.get
    val errUasp = JsonConvertInService.deserialize[UaspDto](dto.value).right.get

    errUasp.dataString.get(errFieldName)
      .map(d=>assertResult("requirement failed: RuleName 'n': Uncompatitible value 'null' for operation Mutate() ")(d))
      .getOrElse(throw new IllegalStateException("not found field "+errFieldName+ " in dataString mal"))

  }

  it should "error on Delete not existing key value" in {
    val service = new BusinessRulesService(_ => {
      Set[UaspOperation](UaspOperation("n", StringMap(null), "asd", Delete()))
    })

    val dto = service.processWithDlq(dtoNoFields).left.get
    val errUasp = JsonConvertInService.deserialize[UaspDto](dto.value).right.get

    assertResult("For operation class ru.vtb.uasp.mutator.service.dto.Delete field 'asd' not found")(errUasp.dataString("sys-BussinesRulles-error"))
  }

  it should "error on multiple mutate one field" in {
    val asd = "asd"
    val service = new BusinessRulesService(_ => {
      Set[UaspOperation](UaspOperation("n", StringMap(null), asd, Delete()),
        UaspOperation("n", StringMap(null), asd, Add()))
    })

    val dto = service.processWithDlq(dtoNoFields).left.get
    val errUasp = JsonConvertInService.deserialize[UaspDto](dto.value).right.get
    errUasp.dataString.get(errFieldName)
      .map(d=>assertResult("requirement failed: drools should not mutate same field Map((asd,class ru.vtb.uasp.mutator.service.dto.StringMap) -> List(UaspOperation(n,StringMap(null),asd,Delete()), UaspOperation(n,StringMap(null),asd,Add()))) ")(d))
      .getOrElse(throw new IllegalStateException("not found field "+errFieldName+ " in dataString mal"))

  }

  private def countVals(dto: UaspDto) = {
    dto.dataString.size + dto.dataDecimal.size + dto.dataInt.size + dto.dataFloat.size + dto.dataLong.size + dto.dataBoolean.size + dto.dataDouble.size
  }
}

object BusinessRulesServiceTest {

  val genUaspOperation: (Int, Set[TypeOperation]) => Set[UaspOperation] = { (n, typeOper) =>
    (1 to n)
      .flatMap(q =>
        List[MapClass](LongMap(q),
          IntMap(q),
          FloatMap(q),
          DoubleMap(q),
          BigDecimalMap(q),
          StringMap(q.toString),
          BooleanMap(q % 2 == 0),
        ).map(t => q -> t)
      )
      .flatMap(v => typeOper.map(t => t -> v))
      .map(t => UaspOperation(
        t._2._1.toString + t._1.toString,
        t._2._2,
        t._2._1.toString + t._2._2.toString,
        t._1)
      )
      .toSet
  }

  val dtoNoFields: UaspDto = UaspDto(id = "id",
    dataInt = Map(),
    dataLong = Map(),
    dataFloat = Map(),
    dataDouble = Map(),
    dataDecimal = Map(),
    dataString = Map(),
    dataBoolean = Map(),
    uuid = "String",
    process_timestamp = Calendar.getInstance.getTimeInMillis)


}