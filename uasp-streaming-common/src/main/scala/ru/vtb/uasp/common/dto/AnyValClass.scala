package ru.vtb.uasp.common.dto


sealed trait AnyValClass {
//  @deprecated
  def value: Any

 def isNotNull: Boolean = Option(value).isDefined
}

final case class StringObj(get: String) extends AnyValClass {
  override def value: String = get

}

final case class DoubleObj(get: Double) extends AnyValClass {
  override def value: Double = get
}

final case class LongObj(get: Long) extends AnyValClass {
  override def value: Long = get

}

final case class IntObj(get: Int) extends AnyValClass {
  override def value: Int = get

}

final case class DecimalObj(get: BigDecimal) extends AnyValClass {
  override def value: BigDecimal = get

}

final case class BooleanObj(get: Boolean) extends AnyValClass {
  override def value: Boolean = get

}

final case class FloatObj(get: Float) extends AnyValClass {
  override def value: Float = get

}

trait AnyValClassWriter[T] {
  def add(v: T): AnyValClass
}


object AnyValClassWriter {
  def apply[T](implicit anyValWriter: AnyValClassWriter[T]): AnyValClassWriter[T] = anyValWriter

  implicit val s: AnyValClassWriter[String] = new AnyValClassWriter[String] {
    def add(v: String): AnyValClass = StringObj(v)
  }

  implicit val i: AnyValClassWriter[Int] = new AnyValClassWriter[Int] {
    def add(v: Int): AnyValClass = IntObj(v)
  }

  implicit val l: AnyValClassWriter[Long] = new AnyValClassWriter[Long] {
    def add(v: Long): AnyValClass = LongObj(v)
  }

  implicit val d: AnyValClassWriter[Double] = new AnyValClassWriter[Double] {
    def add(v: Double): AnyValClass = DoubleObj(v)
  }

  implicit val f: AnyValClassWriter[Float] = new AnyValClassWriter[Float] {
    def add(v: Float): AnyValClass = FloatObj(v)
  }

  implicit val bd: AnyValClassWriter[BigDecimal] = new AnyValClassWriter[BigDecimal] {
    def add(v: BigDecimal): AnyValClass = DecimalObj(v)
  }

  implicit val b: AnyValClassWriter[Boolean] = new AnyValClassWriter[Boolean] {
    def add(v: Boolean): AnyValClass = BooleanObj(v)
  }

  object AnyValSyntax {
     implicit class anyValOps[A](val v:A) extends AnyVal{
       def toAnyValClass(implicit ev: AnyValClassWriter[A]):AnyValClass = ev.add(v)
     }

    implicit class anyValOpsBoolean(val v: Boolean) extends AnyVal {
      def toAnyValClass: AnyValClass = BooleanObj(v)
    }

    implicit class anyValOpsBigDecimal(val v: BigDecimal) extends AnyVal {
      def toAnyValClass: AnyValClass = DecimalObj(v)
    }

    implicit class anyValOpsString(val v: String) extends AnyVal {
      def toAnyValClass: AnyValClass = StringObj(v)
    }

    implicit class anyValOpsDouble(val v: Double) extends AnyVal {
      def toAnyValClass: AnyValClass = DoubleObj(v)
    }

    implicit class anyValOpsLong(val v: Long) extends AnyVal {
      def toAnyValClass: AnyValClass = LongObj(v)
    }

    implicit class anyValOpsInt(val v: Int) extends AnyVal {
      def toAnyValClass: AnyValClass = IntObj(v)
    }

    implicit class anyValOpsFloat(val v: Float) extends AnyVal {
      def toAnyValClass: AnyValClass = FloatObj(v)
    }
  }
}