package ru.vtb.uasp.mutator.service.dto

sealed trait TypeOperation {

  val mustBeSingle: Boolean

  def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T]
}

case class Add() extends TypeOperation {


  override val mustBeSingle: Boolean = true

  override def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T] =
    mapForMutate.get(nameField)
      .map[Map[String, T]](oldVal => throw new IllegalStateException(s"For operation ${this.getClass} field '$nameField' must be null, current value is '$oldVal'"))
      .getOrElse(mapForMutate + (nameField -> newValue.getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} for field '$nameField' added value must be not null"))))
}

case class Delete() extends TypeOperation {

  override val mustBeSingle: Boolean = true

  override def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T] =
    mapForMutate.get(nameField)
      .map[Map[String, T]](_ => mapForMutate - nameField)
      .getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} field '$nameField' not found"))
}

case class Mutate() extends TypeOperation {

  override val mustBeSingle: Boolean = true

  override def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T] =
    mapForMutate.get(nameField)
      .map[Map[String, T]](_ => mapForMutate + (nameField -> newValue.getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} for field '$nameField' added value must be not null")))
      ).getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} field '$nameField' must be not null"))
}

case class ConcatenateStr(delimeter: String = "") extends TypeOperation {

  override val mustBeSingle: Boolean = false

  override def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T] = {
    val value = newValue.getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} field '$nameField' must be not null"))
    if (value.isInstanceOf[String] & mapForMutate.isInstanceOf[Map[String, String]]) {
      val mutateStrMap: Map[String, String] = mapForMutate.asInstanceOf[Map[String, String]]
      val valueStr = value.asInstanceOf[String]

      val newString = mutateStrMap.get(nameField)
        .map(oldVal => s"$oldVal$delimeter$valueStr")
        .getOrElse(valueStr)
      val value1 = mutateStrMap + (nameField -> newString)
      value1.asInstanceOf[Map[String, T]]
    } else {
      throw new IllegalStateException(s"for ${this.getClass.getSimpleName} mapForMutate must be Map[String, String] and newValue must be Option[String]")
    }
  }
}

case class AddOrMutate() extends TypeOperation {

  override val mustBeSingle: Boolean = true

  override def mutate[T](mapForMutate: Map[String, T], newValue: Option[T], nameField: String): Map[String, T] =
    mapForMutate.get(nameField)
      .map[Map[String, T]](_ => mapForMutate + (nameField -> newValue.getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} for field '$nameField' added value must be not null"))))
      .getOrElse(mapForMutate + (nameField -> newValue.getOrElse(throw new IllegalStateException(s"For operation ${this.getClass} for field '$nameField' added value must be not null"))))
}
