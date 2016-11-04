package fr.mleduc.concurrent.poc.programmingscala
import scala.language.higherKinds

object add {
  def sum[T: Add, M[T]](container: M[T])(
    implicit red: Reduce[T, M]): T =
    red.reduce(container)(implicitly[Add[T]].add(_, _))
                                                  //> sum: [T, M[T]](container: M[T])(implicit evidence$2: fr.mleduc.concurrent.po
                                                  //| c.programmingscala.Add[T], implicit red: fr.mleduc.concurrent.poc.programmin
                                                  //| gscala.Reduce[T,M])T

  sum(Vector(1 -> 10, 2 -> 20, 3 -> 30))          //> res0: (Int, Int) = (6,60)
  sum(1 to 10)                                    //> res1: Int = 55
  sum(Option(2))                                  //> res2: Int = 2
  //sum[Int,Option](None)
}