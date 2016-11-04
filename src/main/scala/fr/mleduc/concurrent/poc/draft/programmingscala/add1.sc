package fr.mleduc.concurrent.poc.programmingscala
import fr.mleduc.concurrent.poc.draft.programmingscala.{Add, Reduce1}

import scala.language.higherKinds

object add1 {
  def sum[T: Add, M[_]: Reduce1](container: M[T]): T =
    implicitly[Reduce1[M]].reduce(container)(implicitly[Add[T]].add(_, _))
  //> sum: [T, M[_]](container: M[T])(implicit evidence$3: fr.mleduc.concurrent.po
  //| c.programmingscala.Add[T], implicit evidence$4: fr.mleduc.concurrent.poc.pro
  //| grammingscala.Reduce1[M])T
}