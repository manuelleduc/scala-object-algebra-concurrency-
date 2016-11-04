package fr.mleduc.concurrent.poc.draft.programmingscala

/**
  * Created by mleduc on 27/10/16.
  */
trait Reduce[T, -M[T]] {
  type X[Y] <: TraversableOnce[Y]
  def reduce(m: M[T])(f: (T, T) => T): T
}

object Reduce {
  implicit def seqReduce[T] = new Reduce[T, Seq] {
    override def reduce(m: Seq[T])(f: (T, T) => T): T = m reduce f
  }

  implicit def optionReduce[T] = new Reduce[T, Option] {
    override def reduce(m: Option[T])(f: (T, T) => T): T = m reduce f
  }
}