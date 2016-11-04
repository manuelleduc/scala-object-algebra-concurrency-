package fr.mleduc.concurrent.poc.draft.implicitsvstypeclasses

/**
  * Created by mleduc on 02/11/16.
  */
object Figure3 {

  trait Eq[T] {
    def equal(a: T, b: T): Boolean
  }

  trait Ord[T] extends Eq[T] {
    def compare(a: T, b: T): Boolean

    override def equal(a: T, b: T): Boolean = compare(a, b) && compare(b, a)
  }


  class ReverseOrder[T](orignal: Ord[T]) extends Ord[T] {
    override def compare(a: T, b: T): Boolean = !orignal.compare(a, b)
  }

  class IntOrd extends Ord[Int] {
    override def compare(a: Int, b: Int): Boolean = a <= b
  }

  class ListOrd[T](ordD: Ord[T]) extends Ord[List[T]] {
    override def compare(l1: List[T], l2: List[T]): Boolean = (l1, l2) match {
      case (x :: xs, y :: ys) =>
        if (ordD.equal(x, y)) compare(xs, ys)
        else ordD.compare(x, y)
      case (_, Nil) => false
      case (Nil, _) => true
    }
  }

  class ListOrd2[T](ordD: Ord[T]) extends Ord[List[T]] {
    private val listOrd = new ListOrd[T](ordD)

    override def compare(l1: List[T], l2: List[T]): Boolean =
      (l1.length < l2.length) && listOrd.compare(l1, l2)
  }

}


object Figure3Exec extends App {

  import Figure3._

  val b = List(1, 2, 3)
  val a = List(2, 3, 4)

  private val ordLstInst = new ListOrd[Int](new ReverseOrder[Int](new IntOrd))
  private val res = ordLstInst compare(a, b)
  println(res)

}