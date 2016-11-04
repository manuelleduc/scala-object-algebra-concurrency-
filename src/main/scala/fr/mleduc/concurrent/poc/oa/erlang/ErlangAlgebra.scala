package fr.mleduc.concurrent.poc.oa.erlang

import java.util.UUID

import fr.mleduc.concurrent.poc.oa.kernel.KernelAlgExec

import scala.collection.mutable

/**
  * Created by mleduc on 02/11/16.
  */
trait ErlangAlgebra {
  type ActorId

  def send(actor1: ActorId, data: Any)

  def createActor[A, B](s: PartialFunction[A, B]): ActorId

}


trait ErlangAlgebraExec extends ErlangAlgebra {
  override type ActorId = UUID

  //private val kernel = new KernelAlgExec {}

  private val map: mutable.Map[UUID, Unit] = scala.collection.mutable.Map.empty

  override def send(actor1: UUID, data: Any): Unit = {

  }

  override def createActor[A, B](s: PartialFunction[A, B]): UUID = {
    val ret: UUID = UUID.randomUUID()
    map.put(ret, s)
    ret
  }
}
