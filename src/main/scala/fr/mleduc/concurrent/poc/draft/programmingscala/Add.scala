package fr.mleduc.concurrent.poc.draft.programmingscala

/**
  * Created by mleduc on 27/10/16.
  */
trait Add[T] {
  def add(t1: T, t2: T): T
}

object Add {
  implicit val addInt = new Add[Int] {
    override def add(t1: Int, t2: Int) = t1 + t2
  }

  implicit val addIntIntPair = new Add[(Int, Int)] {
    override def add(t1: (Int, Int), t2: (Int, Int)) = (t1._1 + t2._1, t1._2 + t2._2)
  }
}

object TestAdd {
  def sumSeq[T: Add](seq: Seq[T]): T =
    seq reduce (implicitly[Add[T]].add(_, _))
}