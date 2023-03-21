package ru.vtb.uasp.common.generate

import ru.vtb.uasp.common.generate.ConstCreateEnv.{generatorPropList, placeholders, rootFolder}
import ru.vtb.uasp.common.generate.dto._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigUtils, ConfigurationInitialise}

import java.io.{BufferedWriter, File, FileWriter}

@deprecated
object ConfigurationChecker {

  def createConfiguration[T](filePrefix: String, profiles: List[Profile], clazz: Class[_], combiner: ConfigurationInitialise[T], propertyPrefix: String): List[T] = {
    implicit val clazz1: Class[_] = clazz
    implicit val profiles1: List[Profile] = profiles


    val fld = s"./target/$rootFolder/stages"
    val confDeploymentDir = new File(fld)
    confDeploymentDir.mkdirs()


    generatorPropList.map { p =>

      implicit val properties: AllApplicationProperties = ConfigUtils.readAllPropsByProfile(
        profile = p.profile.profileName,
        isExample = true
      )


      //формирование по честному всех пропертей и проверка на наличии ошибок по всем генерируемым профилям
      val errorsOrModel = combiner.create(propertyPrefix)(properties, combiner)

      if (errorsOrModel.isLeft) {
        val value = errorsOrModel.left.get.errors.mkString("\n")
        throw new IllegalStateException(s"error for profile ${p.profile}, error: \n $value")
      }

      val profile = p.profile

      checkNotUsed(combiner, properties, profile)


      val folderForStand = fld + "/" + p.stand.folderName
      new File(folderForStand).mkdir()

      createEnvironment(folderForStand, p.creationEnvProp, p.stand, p.profile, filePrefix)

      errorsOrModel.right.get


    }.toList

  }


  private def checkNotUsed[T](combiner: ConfigurationInitialise[T], properties: AllApplicationProperties, profile: Profile): Unit = {
    val keysNotRead = properties.prop.keys.filter(key =>
      !combiner.readKey.contains(key)
    ).toList

    if (keysNotRead.nonEmpty) {
      val value = keysNotRead.mkString("\n")
      val reads = combiner.readKey.mkString("\n")
      throw new IllegalStateException(s"Not used ${keysNotRead.size} key for profile $profile \n$value\n READS VALUE:\n$reads")
    }
  }

  private def createEnvironment(
                                 folderForStand: String,
                                 prop: CreationEnvProp,
                                 stand: StandDTO,
                                 profile: Profile,
                                 serviceName: String

                               )(implicit properties: AllApplicationProperties): Unit = {
    var headPlaceHolders: Map[String, String] = Map()
    val priorities = properties.prop.toList
      .map(pr => {
        val key = pr._1
        val placeHolder = placeholders.keySet.filter(p => key.contains(p)).toList
        val newValue = placeHolder match {
          case Nil => ReplaceDto(pr._2)
          case x :: Nil => ReplaceDto(pr._2, Some(placeholders(x)))
          case x :: xs => throw new IllegalStateException(s"for key $key found several placeholders ${x :: xs}")
        }
        val splittenKey = key.split('.')
        val upToHeadPlaceHolder = splittenKey.mkString("_")


        if (splittenKey(splittenKey.length - 1).toLowerCase().contains("topic")) {
          val placeHolder = upToHeadPlaceHolder.toUpperCase().replace("-", "_").replace("$", "")
          val newTopicName = pr._2.replace(stand.topicPrefixDefault, stand.topicPrefixReplace)
          headPlaceHolders = headPlaceHolders + (placeHolder -> newTopicName)
          PropertyWithPriority(Prop(key, s"$${$placeHolder}"), 10)
        }
        else if (newValue.placeholder.isDefined) {
          if (newValue.placeholder.get.upToHead) {
            headPlaceHolders = headPlaceHolders + (newValue.placeholder.get.replaceValue -> pr._2)
            val value = newValue.placeholder.get
              .calcNewValue.map(q => q)
              .getOrElse(s"$${${newValue.placeholder.get.replaceValue}}")

            PropertyWithPriority(Prop(key, value), 10)
          }
          else {
            val newPlaceholder = newValue.placeholder.get.replaceValue.replace(stand.placeholderDefault, stand.placeholderReplace)
            PropertyWithPriority(Prop(key, newPlaceholder), 80)
          }
        } else PropertyWithPriority(Prop(key, newValue.value), 10)

      })
    val body = priorities
      .foldLeft(Map[Int, List[Prop]]()) { (acc, o) =>
        val newList = acc.get(o.priority)
          .map(l => o.prop :: l)
          .getOrElse(List(o.prop))
        acc + (o.priority -> newList)
      }
      .toList
      .sortBy(_._1).reverse
      .map { v =>
        v._2
          .sortBy(_.key)
          .map(entry => s"${entry.key}${prop.keyValueDelimer}${entry.value}")
          .map(p => prop.prefix + p + prop.postFix)
          .mkString(prop.propertyDelimer)
      }
      .mkString(prop.propertyDelimer.replace("\n", "\n\n"))

    val strings = headPlaceHolders.map(q => q._1 + "=" + q._2).toList.sorted
    val headPlaceholdersStr = strings.mkString("\n")

    val file = new File(s"$folderForStand/$serviceName.${profile.profileName}.env")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(headPlaceholdersStr + "\n" + prop.head + body)
    bw.close()
  }
}
