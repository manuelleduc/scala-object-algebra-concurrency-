package fr.mleduc.concurrent.poc.oa.erlang

/**
  * Created by mleduc on 02/11/16.
  */
object ProgramErlang extends App {
  def fridge2(alg: ErlangAlg) = {
    // example from http://learnyousomeerlang.com/more-on-multiprocessing

    sealed trait FridgeMessage
    final case class Store(actorId: alg.ActorId, food: String) extends FridgeMessage
    final case class Take(actorId: alg.ActorId, food: String) extends FridgeMessage
    final case class Ok(option: Option[String]) extends FridgeMessage
    final case class NotFound() extends FridgeMessage

    lazy val kitchen2: (alg.ActorId) => PartialFunction[Any, Unit] = self => {

      case foods: List[String] =>
        alg.receive(self, {
          case Store(actorId, food) =>

            alg.send(actorId, Ok(None))
            alg.respawn(kitchen2, self, foods + food)
          case Take(actorId, food) =>
            if (food.contains(food)) {
              alg.send(actorId, Some(food))
              alg.respawn(kitchen2, self, foods.filter(_ != food))
            } else {
              alg.send(actorId, NotFound())
            }
        })

    }

    def store(pid: alg.ActorId, self: alg.ActorId): Unit = {
      alg.send(pid, Store(self, "Burger"))
      alg.receive(self, { case x =>
        println(s"STORE RES $x")
        take(pid, self)
      })
    }

    def take(pid: alg.ActorId, self: alg.ActorId): Unit = {
      alg.send(pid, Take(self, "Burger"))
      alg.receive(self, { case x => println(s"TAKE RES $x") })
    }

    val main: (alg.ActorId) => (alg.ActorId) => PartialFunction[Any, Unit] = pid => self => {
      case _ => store(pid, self)
    }


    val pid: alg.ActorId = alg.spawn(kitchen2, List.empty)
    alg.spawn(main(pid), ())
    while (true) alg.execAll()
  }


  fridge2(new ErlangAlgExec {})
}
