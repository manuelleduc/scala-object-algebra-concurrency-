package fr.mleduc.concurrent.poc.oa.erlang

/**
  * Created by mleduc on 02/11/16.
  */
object ProgramErlang extends App {
  def fridge2(alg: ErlangAlg) = {
    // example from http://learnyousomeerlang.com/more-on-multiprocessing

    import alg._

    sealed trait FridgeMessage
    final case class Store(actorId: ActorId, food: String) extends FridgeMessage
    final case class Take(actorId: ActorId, food: String) extends FridgeMessage
    final case class Ok(option: Option[String]) extends FridgeMessage
    final case class NotFound() extends FridgeMessage

    lazy val kitchen2: (ActorId) => PartialFunction[Any, Unit] = self => {

      case foods: List[String] =>
        receive(self, {
          case Store(actorId, food) =>
            send(actorId, Ok(None))
            respawn(kitchen2, self, foods + food)
          case Take(actorId, food) =>
            if (food.contains(food)) {
              send(actorId, Some(food))
              respawn(kitchen2, self, foods.filter(_ != food))
            } else {
              send(actorId, NotFound())
            }
        })
    }

    def store(pid: ActorId, self: ActorId): Unit = {
      send(pid, Store(self, "Burger"))
      receive(self, { case x =>
        println(s"STORE RES $x")
        take(pid, self)
      })
    }

    def take(pid: ActorId, self: ActorId): Unit = {
      send(pid, Take(self, "Burger"))
      receive(self, { case x => println(s"TAKE RES $x") })
    }

    val main: (ActorId) => (ActorId) => PartialFunction[Any, Unit] = pid => self => {
      case _ => store(pid, self)
    }


    val pid: ActorId = spawn(kitchen2, List.empty)
    spawn(main(pid), ())
  }


  fridge2(new ErlangAlgExec {})
}
