package fr.mleduc.concurrent.poc.oa.erlang

import java.util.UUID
import java.util.concurrent.Executors

import fr.mleduc.concurrent.poc.oa.kernel.KernelAlgExec

import scala.collection.mutable
import scala.concurrent.ExecutionContext

/**
  * Created by mleduc on 02/11/16.
  */
trait ErlangAlgebra {
  def execAll()

  def respawn(actor: ActorId => PartialFunction[Any, Unit], pid: ActorId, constructor: Any): Unit

  def debug()

  type ActorId

  def send(actor1: ActorId, data: Any)

  def spawn(actor: ActorId => PartialFunction[Any, Unit], constructor: Any): ActorId

  def receive(self: ActorId, continuation: PartialFunction[Any, Unit])

}


trait ErlangAlgebraExec extends ErlangAlgebra {
  override type ActorId = UUID

  private val kernel = new KernelAlgExec {}

  private val actorToOperation: mutable.Map[UUID, UUID] = scala.collection.mutable.Map.empty

  implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool)


  override def send(actor: UUID, data: Any): Unit = {
    //println(s"send $data to $actor")

    val maybeUuid: Option[UUID] = actorToOperation.get(actor)
    maybeUuid.foreach(broadcast => {
      kernel.broadcastMessage(broadcast, data)
    })
    kernel.execAll()

  }

  override def spawn(actor: ActorId => PartialFunction[Any, Unit], constructor: Any): UUID = {
    val actorId = UUID.randomUUID()

    val actor1: PartialFunction[Any, Unit] = actor(actorId)
    val opp: UUID = kernel.spawnOperation(actor1)
    kernel.startOperation(opp, constructor)
    actorId

  }


  override def respawn(actor: (UUID) => PartialFunction[Any, Unit], pid: UUID, constructor: Any): Unit = {

    val actor1: PartialFunction[Any, Unit] = actor(pid)
    val opp: UUID = kernel.spawnOperation(actor1)
    kernel.startOperation(opp, constructor)


  }

  override def receive(self: UUID, continuation: PartialFunction[Any, Unit]): Unit = {


    val broadcast = kernel.createPersistentBroadcast()
    //println(s"broadcast uuid = $broadcast")
    kernel.registerToBroadcast(broadcast, continuation)
    actorToOperation.put(self, broadcast)

  }

  override def debug(): Unit = kernel.debug()

  override def execAll(): Unit = kernel.execAll()
}
