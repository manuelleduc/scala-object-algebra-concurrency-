package fr.mleduc.concurrent.poc.oa.kernel

import java.util.UUID

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.mutable
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

  def execAll(): Unit
}


trait KernelAlgExec extends KernelAlg {



  override def debug(): Unit = println(opGraph)

  override type OperationId = UUID

  sealed trait GraphOperations

  final case class OperationalNode(uuid: OperationId, operation: PartialFunction[Any, Unit], message: Option[Any] = None) extends GraphOperations

  final case class BroadcastNode(uuid: OperationId) extends GraphOperations

  private val operationMap: mutable.Map[OperationId, PartialFunction[Any, Unit]] = mutable.Map.empty

  // TODO : continuer a implem sur la base d'un graph. Lancer résolution du problème dans un thread à part ?
  private val opGraph = Graph[GraphOperations, DiEdge]()

  /**
    * This type of actor broadcast a message to everybody subscribed to it.
    * One a message is send to an subscribed actor, this actor is unsubscribed
    *
    * @return
    */
  override def createBroadcast(): OperationId = {
    val id = UUID.randomUUID()
    //opGraph = opGraph + BroadcastNode(id)
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
    //opGraph.add(OperationalNode(actorId, pf))
    pf()
  }
}


trait KernelAlgExecActors /*extends KernelAlg*/ {
  /*override */ type OperationId = ActorRef

  private val system = ActorSystem("BroadActorSystem")

  //private val uuidToActors: mutable.Map[ActorId, ActorRef] = mutable.Map.empty


  sealed trait BroadcastActorActions

  final case class RegisterMe(actorRef: ActorRef)

  final case class BroadcastThis(message: Any)

  class OperationalActor(receivez: Receive) extends Actor {
    override def receive: Receive = {
      receivez
    }
  }

  class BroadcastActor() extends Actor {

    private val registered: scala.collection.mutable.Set[ActorRef] = mutable.Set.empty


    override def receive: Receive = {

      case RegisterMe(actorRef) => {
        registered.add(actorRef)
      }
      case BroadcastThis(message) =>
        /*
        Once every message is broadcasted, the registration list is cleared.
         */
        registered foreach (actorId => {
          actorId ! message
        })
        registered.clear()
    }
  }

  /**
    * This type of actor broadcast a message to everybody subscribed to it.
    * One a message is send to an subscribed actor, this actor is unsubscribed
    *
    * @return
    */
  /*override*/ def createBroadcast(): OperationId = {
    /*val broadcastActor: ActorRef = this.system.actorOf(Props(new BroadcastActor()))
    val id: UUID = UUID.randomUUID()
    uuidToActors.put(id, broadcastActor)
    id*/
    this.system.actorOf(Props(new BroadcastActor()))
  }

  /*override*/ def broadcastMessage(actorId: OperationId, message: Any): Unit = {
    actorId ! BroadcastThis(message)
  }


  /*override*/ def registerToBroadcast(actorId: OperationId, continuation: PartialFunction[Any, Unit]): Unit = {
    val operationalActor = system.actorOf(Props(new OperationalActor(continuation)))
    actorId ! RegisterMe(operationalActor)
  }

  /*override*/ def spawnOperation(operation: PartialFunction[Any, Unit]): OperationId = {

    system.actorOf(Props(new OperationalActor(operation)))
  }

  /*override*/ def startOperation(actorId: ActorRef): Unit = actorId ! ()
}