package fr.mleduc.concurrent.poc.draft

import java.util.UUID


trait Language {
  type Signal
  type Process

  def signal(): Signal

  def createProcess(processAction: Any => PartialFunction[Any, Unit]): Process

  def startProcess(process: Process)

  /**
    * Creating a channel create a "callback" channel.
    *
    * @param channel input channal
    * @param data    send data
    * @return the resulting channel
    */
  def emit(channel: Signal, data: Any): Signal

  //def bind[A](channel: Signal, opp: Signal =>): Unit

  //def parallel[S, T, U](l: S => Opp[T], r: S => Opp[U]): S => Opp[Any]

  //def finish[A, B](l: A => B): A => Opp[B]
}

trait LanguageImpl extends Language {

  override type Signal = UUID
  var channelsCollection: Map[UUID, Set[(UUID) => (Any) => PartialFunction[Any, Unit]]] = Map.empty

  override def signal(): UUID = {
    val uuid: UUID = UUID.randomUUID()
    val set: Set[(UUID) => (Any) => PartialFunction[Any, Unit]] = Set.empty
    val tmp: Map[UUID, Set[(UUID) => (Any) => PartialFunction[Any, Unit]]] = channelsCollection + ((uuid, set))
    channelsCollection = tmp
    uuid
  }

  /**
    * Creating a channel create a "callback" channel.
    *
    * @param channel input channal
    * @param data    send data
    * @return the resulting channel
    */
  /*override def send(channel: UUID, data: Any): UUID = {
    val channelOp: Option[Set[(UUID) => (Any) => PartialFunction[Any, Unit]]] = channelsCollection.get(channel)
    val outputChannel = signal()
    channelOp foreach (_ foreach { actor => actor(outputChannel)(data) })
    outputChannel
  }

  override def bind[A](channel: UUID, opp: (UUID) => (Any) => PartialFunction[Any, Unit]): Unit =
    channelsCollection = channelsCollection + ((channel, (channelsCollection.get(channel) getOrElse Set.empty) + opp))*/

}

/*

a
b
def signal a

bind a opp
   // partial function <- used into the actor
bind a opp2

send a Data
 */