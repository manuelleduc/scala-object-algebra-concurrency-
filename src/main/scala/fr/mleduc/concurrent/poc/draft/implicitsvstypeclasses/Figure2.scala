package fr.mleduc.concurrent.poc.draft.implicitsvstypeclasses

/**
  * Created by mleduc on 02/11/16.
  */
object Figure2 {

  trait Ord[T] {
    def compare(x: T, y: T): Boolean
  }

  implicit def OrdPair[A, B]
  (implicit ordA: Ord[A], ordB: Ord[B])
  = new Ord[(A, B)] {
    override def compare(x: (A, B), y: (A, B)): Boolean = ordA.compare(x._1, y._1) && ordB.compare(x._2, y._2)
  }

  def cmp[a: Ord](x: a, y: a): Boolean = implicitly[Ord[a]].compare(x, y)

  class Apple(val x: Int) {}

  object ordApple extends Ord[Apple] {
    override def compare(a1: Apple, a2: Apple): Boolean = a1.x <= a2.x
  }

  def pick[T](a1: T, a2: T)(ordA: Ord[T]) =
    if (ordA.compare(a1, a2)) a2 else a1

  val a1 = new Apple(3)
  val a2 = new Apple(5)
  val a3 = pick(a1, a2)(ordApple)

  trait Ordered[T] {
    def compare(o: T): Boolean
  }

  implicit def mkOrd[T: Ord](x: T): Ordered[T] = new Ordered[T] {
    override def compare(that: T): Boolean = implicitly[Ord[T]].compare(x, that)
  }

}

object Figure2Exec extends App {

  import Figure2.{a1, a2, a3}

  println(s"a1=${a1.x}, a2=${a2.x}, a3=${a3.x}")


  /*implicit import  Figure2.ordApple
  println(cmp(a1, a2))*/

  println(1 min 2)
  //val a: (Int) => {def min(i: Int): Int} = implicitly[Int => { def min(i: Int): Int }]

  //println(a(1))
}
