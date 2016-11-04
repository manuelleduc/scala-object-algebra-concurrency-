package fr.mleduc.concurrent.poc.draft.tesst

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait MonadTrait {

  type Monad[X]

  def lift[A](a: A): Monad[A]

  def ap[A, B](fct: Monad[A => B], param: Monad[A]): Monad[B]

  def bind[A, B](fct: Monad[A], f: A => Monad[B]): Monad[B]

  def merge[A,B](fct: Monad[A], f: A => Monad[B]): Monad[A=>B]

  implicit def valToFuture[A](elem: A): Monad[A] = lift(elem)

}


object FutureMonad extends MonadTrait {
  override type Monad[X] = Future[X]

  override def lift[A](a: A): Monad[A] = Future(a)

  override def ap[A, B](fct: Monad[A => B], param: Monad[A]): Monad[B] =
    for {
      c <- param
      o <- fct
    } yield (o(c))

  override def merge[A, B](fct: Future[A], f: (A) => Future[B]): Future[A => B] = {
    /*a => {

    }*/
    ???
  }

  override def bind[A, B](fa: Monad[A], f: A => Monad[B]): Monad[B] =
    fa.flatMap { x => f(x) }


  implicit class FutureInfix[A, B](val x: Monad[A => B]) extends AnyVal {
    def <*>(that: Monad[A]) = ap(this.x, that)
  }

}

object Test1 extends App {

  import FutureMonad._

  val plus1: Int => Int = (x: Int) => x + 1
  val cst1: Int = 1

  val extracted: Future[Int] = ap(plus1, cst1)
  val extracted2: Future[Int] = lift(plus1) <*> lift(cst1)
  //  val extracted3: Future[Int] = (plus1: Future[Int => Int]) <*> (cst: Future[Int])
  /*
   * - value <*> is not a member of Int ⇒ Int
   * - not found: value cst
   */

  val res: Future[Int] = bind(1, (x: Int) => x + 2)

  println("start")
  res.onComplete {
    case Success(x) => println(x)
    case Failure(x) => println(x)
  }
}
