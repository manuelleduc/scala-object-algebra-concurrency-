package fr.mleduc.concurrent.poc.draft.implicitsvstypeclasses

object Figure1 {

  trait Monoid[A] {
    def binary_oop(x: A, y: A): A

    def identity: A
  }

  def acc[A](l: List[A])(implicit m: Monoid[A]): A =
    (l foldLeft m.identity) (m binary_oop(_, _))

  object A {

    implicit object sumMonoid extends Monoid[Int] {
      override def binary_oop(x: Int, y: Int): Int = x + y

      override def identity: Int = 0
    }

    def sum(l: List[Int]): Int = acc(l)
  }

  object B {

    implicit object prodMonoid extends Monoid[Int] {
      override def binary_oop(x: Int, y: Int): Int = x * y

      override def identity: Int = 1
    }

    def product(l: List[Int]): Int = acc(l)

  }

  val test: (Int, Int, Int) = {
    import A._
    import B._

    val l = List(1, 2, 3, 4, 5)
    (sum(l), product(l), acc(l)(prodMonoid))
  }

}

object Figure1Exec extends App {
  println(Figure1.test)
}
