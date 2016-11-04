package fr.mleduc.concurrent.poc.draft.implicitsvstypeclasses

/**
  * Created by mleduc on 02/11/16.
  */
object Figure6 {

  trait Format[A] {
    def formatz(s: String): A
  }

  def printf[A](format: Format[A]): A = format.formatz("")

  class I[A](formatD: Format[A]) extends Format[Int => A] {
    override def formatz(s: String): (Int) => A = i =>
      formatD.formatz(s + i.toString)
  }

  class C[A](formatD: Format[A]) extends Format[Char => A] {
    override def formatz(s: String): (Char) => A = c =>
      formatD.formatz(s + c.toString)
  }

  class E extends Format[String] {
    override def formatz(s: String): String = s
  }

  class S[A](l: String, formatD: Format[A]) extends Format[A] {
    override def formatz(s: String): A = formatD.formatz(s + l)
  }

}


object Figure6Exec extends App {

  import Figure6._

  private val formatD: E = new E
  private val formatD1: S[String] = new S(" hello", formatD)
  private val formatD2: C[String] = new C(formatD1)
  private val format: S[(Char) => String] = new S("Char ", formatD2)
  private val printf2: (Char) => String = printf(format)
  println(printf2('x'))
}