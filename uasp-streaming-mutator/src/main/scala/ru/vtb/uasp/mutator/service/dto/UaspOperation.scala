package ru.vtb.uasp.mutator.service.dto

case class UaspOperation(droolsName: String,
                         typeField: MapClass,
                         nameField: String,
                         typeOperation: TypeOperation,
                        ) {

  def errorOperation: Option[String] =
    (typeOperation, typeField.isNotNull) match {
      case (Add(), true) | (Mutate(), true) | (AddOrMutate(), true) | (Delete(), _) => None
      case (ConcatenateStr(_), true) => None
      case (_, _) => Some(s"RuleName '$droolsName': Uncompatitible value '${typeField.value}' for operation $typeOperation ")
    }

}
