package fr.mleduc.concurrent.poc.oa.erlang

/**
  * Created by mleduc on 02/11/16.
  */
object Program extends App {
  def program1(alg: ErlangAlgebra) = {
    val function: PartialFunction[Any, Unit] = {
      case x => println(x)
    }
    val actor2 = alg createActor function

    val actor1Fct: PartialFunction[List[_], Unit] = {
      case Nil => ()
      case xs => alg.send(actor2, xs.length)
    }
    val actor1 = alg createActor actor1Fct

    alg.send(actor1, List(1))
  }
}
