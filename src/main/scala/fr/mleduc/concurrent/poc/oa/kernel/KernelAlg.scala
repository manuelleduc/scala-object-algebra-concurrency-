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

  def createPersistentBroadcast(): OperationId

  def broadcastMessage(operationId: OperationId, message: Any): Unit

  def registerToBroadcast(operationId: OperationId, continuation: PartialFunction[Any, Unit])

  def spawnOperation(operation: PartialFunction[Any, Unit]): OperationId

  def startOperation(operationId: OperationId, value: Any = Unit): Unit

  def debug(): Unit

  def execAll()(implicit ex: ExecutionContext): Unit

}


trait KernelAlgExec extends KernelAlg {

  override def debug(): Unit = println(opGraph.clone())

  override type OperationId = UUID

  sealed trait GraphOperations {
    val uuid: OperationId
  }

  final case class OperationalNode(uuid: OperationId, operation: PartialFunction[Any, Unit], message: Option[Any] = None) extends GraphOperations

  final case class BroadcastNode(uuid: OperationId) extends GraphOperations

  final case class PersistentBroadcastNode(uuid: OperationId, messages: mutable.Queue[Any] = mutable.Queue.empty) extends GraphOperations

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
    synchronized {
      opGraph.add(BroadcastNode(id))
    }
    id
  }


  override def createPersistentBroadcast(): UUID = {
    val id = UUID.randomUUID()
    synchronized {
      opGraph.add(PersistentBroadcastNode(id))
    }
    id
  }


  override def broadcastMessage(actorId: OperationId, content: Any): Unit = {
    synchronized {
      val res: Option[opGraph.NodeT] = opGraph.nodes.find((x: opGraph.NodeT) => x.value.uuid == actorId)
      res.foreach((nodeT: opGraph.NodeT) => {
        val currentBroadcastNode = nodeT.value

        currentBroadcastNode match {
          case BroadcastNode(id) =>
            val predecessors = nodeT.diPredecessors

            /* we forward the content to all the registered to the broadcast */
            predecessors.foreach(x => {
              val value: GraphOperations = x.value match {
                case OperationalNode(uuid, operation, _) => OperationalNode(uuid, operation, Some(content))
                case _ => x.value
              }
              opGraph.add(value ~> currentBroadcastNode)
              opGraph.remove(x)
            })
            opGraph.remove(currentBroadcastNode)
          case PersistentBroadcastNode(uuid, messages) => {
            messages += content

            val predecessors = nodeT.diPredecessors

            /* we forward the content to all the registered to the broadcast */
            predecessors foreach (x => {
              val value: GraphOperations = x.value match {
                case OperationalNode(uuid, operation, _) => OperationalNode(uuid, operation, Some(content))
                case _ => x.value
              }
              opGraph.add(value ~> currentBroadcastNode)
              opGraph.remove(x)
            })
          }
        }

      })
    }
  }

  override def registerToBroadcast(actorId: OperationId, continuation: PartialFunction[Any, Unit]): Unit =
    synchronized {
      val res = opGraph.nodes.find((x: opGraph.NodeT) => x.value.uuid == actorId)
      res.foreach(elem => {
        elem.value match {
          case BroadcastNode(_) => opGraph.add(OperationalNode(UUID.randomUUID(), continuation) ~> elem.value)
          case PersistentBroadcastNode(uuid, messages) =>
            if (messages.isEmpty) opGraph.add(OperationalNode(UUID.randomUUID(), continuation) ~> elem.value)
            else opGraph.add(OperationalNode(UUID.randomUUID(), continuation, Some(messages.dequeue())))

        }
      })

    }


  override def spawnOperation(operation: PartialFunction[Any, Unit]): OperationId = {
    val id: UUID = UUID.randomUUID()
    synchronized {
      operationMap.put(id, operation)
    }
    id
  }

  override def startOperation(actorId: OperationId, value: Any = Unit): Unit =
    synchronized {
      operationMap.getOrElse(actorId, PartialFunction.empty)(value)
    }


  override def execAll()(implicit ex: ExecutionContext): Unit = {
    synchronized {
      opGraph.nodes
        .filter(opGraph.get(_).diSuccessors.isEmpty)
        .filter(t => t.value match {
          case OperationalNode(_, _, Some(_)) => true
          case PersistentBroadcastNode(_, queue) => queue.nonEmpty && t.diPredecessors.nonEmpty
          case _ => false
        }).foreach((x: opGraph.NodeT) => {
        x.value match {
          case OperationalNode(_, partialFunction, Some(value)) =>
            Future {
              partialFunction(value)
            }
            opGraph.remove(x)
          case PersistentBroadcastNode(_, queue) =>
            val value = queue.dequeue()

            val predecessors = x.diPredecessors
            predecessors.foreach(tmp => {
              tmp.value match {
                case OperationalNode(_, partialFunction, _) =>
                  Future(partialFunction(value))
                  opGraph.remove(tmp)
              }
            })

        }
      })
    }
  }
}