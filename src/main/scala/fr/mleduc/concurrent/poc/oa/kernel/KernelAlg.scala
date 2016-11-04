package fr.mleduc.concurrent.poc.oa.kernel

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

/**
  * Created by mleduc on 02/11/16.
  */
trait KernelAlg {
  type OperationId

  /**
    * This type of actor broadcast a message to everybody subscribed to it.
    * One a message is send to an subscribed actor, this actor is unsubscribed
    *
    * @return
    */
  def createBroadcast(): OperationId

  def broadcastMessage(actorId: OperationId, message: Any): Unit

  def registerToBroadcast(actorId: OperationId, continuation: PartialFunction[Any, Unit])

  def spawnOperation(operation: PartialFunction[Any, Unit]): OperationId

  def startOperation(actorId: OperationId): Unit

  def debug(): Unit

  def execAll()(implicit ex:ExecutionContext): Unit

}


trait KernelAlgExec extends KernelAlg {

  override def debug(): Unit = println(opGraph)

  override type OperationId = UUID

  sealed trait GraphOperations

  final case class OperationalNode(uuid: OperationId, operation: PartialFunction[Any, Unit], message: Option[Any] = None) extends GraphOperations

  final case class BroadcastNode(uuid: OperationId) extends GraphOperations

  private val operationMap: mutable.Map[OperationId, PartialFunction[Any, Unit]] = mutable.Map.empty

  private val opGraph = Graph[GraphOperations, DiEdge]()

  /**
    * This type of actor broadcast a message to everybody subscribed to it.
    * One a message is send to an subscribed actor, this actor is unsubscribed
    *
    * @return
    */
  override def createBroadcast(): OperationId = {
    val id = UUID.randomUUID()
    opGraph.add(BroadcastNode(id))
    id
  }

  override def broadcastMessage(actorId: OperationId, content: Any): Unit = {
    val currentBroadcastNode: BroadcastNode = BroadcastNode(actorId)
    val predecessors: Set[opGraph.NodeT] = opGraph.get(currentBroadcastNode).diPredecessors

    /* we forward the content to all the registered to the broadcast */
    predecessors.foreach((x: opGraph.NodeT) => {
      val value: GraphOperations = x.value match {
        case OperationalNode(uuid, operation, _) => OperationalNode(uuid, operation, Some(content))
        case _ => x.value
      }
      opGraph.add(value ~> currentBroadcastNode)
      opGraph.remove(x)
    })
    opGraph.remove(currentBroadcastNode)
  }

  override def registerToBroadcast(actorId: OperationId, continuation: PartialFunction[Any, Unit]): Unit =
    opGraph.add(OperationalNode(UUID.randomUUID(), continuation) ~> BroadcastNode(actorId))


  override def spawnOperation(operation: PartialFunction[Any, Unit]): OperationId = {
    val id: UUID = UUID.randomUUID()
    operationMap.put(id, operation)
    id
  }

  override def startOperation(actorId: OperationId): Unit = {
    val pf = operationMap.getOrElse(actorId, PartialFunction.empty)
    pf()
  }

  override def execAll()(implicit ex:ExecutionContext): Unit = {
    opGraph.nodes
      .filter(opGraph.get(_).diSuccessors.isEmpty)
      .filter(_.value match {
        case OperationalNode(_, _, Some(_)) => true
        case _ => false
      }).foreach((x: opGraph.NodeT) => {
      x.value match {
        case OperationalNode(_, partialFunction, Some(value)) => {
          Future(partialFunction(value))
          opGraph.remove(x)
        }
      }
    })
  }
}