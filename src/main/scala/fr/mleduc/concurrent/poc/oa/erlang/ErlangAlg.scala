package fr.mleduc.concurrent.poc.oa.erlang

import java.util.UUID
import java.util.concurrent.Executors

import fr.mleduc.concurrent.poc.oa.kernel.KernelAlgExec

import scala.concurrent.ExecutionContext

/**
  * Created by mleduc on 02/11/16.
  */
trait ErlangAlg {
  type ActorId

  def execAll()

  def respawn(actor: ActorId => PartialFunction[Any, Unit], pid: ActorId, constructor: Any): Unit

  def debug()

  def send(actor1: ActorId, data: Any)

  def spawn(actor: ActorId => PartialFunction[Any, Unit], constructor: Any): ActorId

  def receive(self: ActorId, continuation: PartialFunction[Any, Unit])

}


trait ErlangAlgExec extends ErlangAlg {
  override type ActorId = UUID
  implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool)
  private val kernel = new KernelAlgExec {}

  override def send(actor: UUID, data: Any): Unit = {
    kernel.broadcastMessage(actor, data)
    kernel.execAll()
  }

  override def spawn(actor: ActorId => PartialFunction[Any, Unit], constructor: Any): UUID = {
    val id = kernel.createPersistentBroadcast()
    val initilizedActor = actor(id)
    kernel.startOperation(kernel.spawnOperation(initilizedActor), constructor)
    id

  }


  override def respawn(actor: (UUID) => PartialFunction[Any, Unit], pid: UUID, constructor: Any): Unit = {
    val actor1: PartialFunction[Any, Unit] = actor(pid)
    kernel.startOperation(kernel.spawnOperation(actor1), constructor)
  }

  override def receive(self: UUID, continuation: PartialFunction[Any, Unit]): Unit = {
    kernel.registerToBroadcast(self, continuation)
  }

  override def debug(): Unit = kernel.debug()

  override def execAll(): Unit = kernel.execAll()
}
