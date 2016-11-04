package fr.mleduc.concurrent.poc.programmingscala.typelambdas
import scala.language.higherKinds
import fr.mleduc.concurrent.poc.programmingscala.typelambdas.Functor._

object FunctorSC {

  List(1, 2, 3) map2 (_ * 2)                      //> res0: Seq[Int] = List(2, 4, 6)
  Option(2) map2 (_ * 2)                          //> res1: Option[Int] = Some(4)
  val m = Map("one" -> 1, "two" -> 2, "three" -> 3)
                                                  //> m  : scala.collection.immutable.Map[String,Int] = Map(one -> 1, two -> 2, th
                                                  //| ree -> 3)
  m map2 (_ * 2)                                  //> res2: Map[String,Int] = Map(one -> 2, two -> 4, three -> 6)
}