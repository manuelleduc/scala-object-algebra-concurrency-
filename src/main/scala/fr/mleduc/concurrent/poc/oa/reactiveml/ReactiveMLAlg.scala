package fr.mleduc.concurrent.poc.oa.reactiveml

import java.util.UUID
import java.util.concurrent.Executors

import fr.mleduc.concurrent.poc.oa.kernel.KernelAlgExec

import scala.concurrent.ExecutionContext

/**
  * Created by mleduc on 02/11/16.
  */
trait ReactiveMLAlg {
  def debug()

  type Signal

  type Process

  def signal(): Signal

  def await(s: Signal)(continuation: PartialFunction[Any, Unit])

  def createProcess(processAction: PartialFunction[Any, Unit]): Process

  def startProcess(process: Process): Unit

  /**
    * Creating a channel create a "callback" channel.
    *
    * @param channel input channal
    * @param data    send data
    * @return the resulting channel
    */
  def emit(channel: Signal, data: Any): Unit
}

trait ReactiveMLAlgExec extends ReactiveMLAlg {
  override type Signal = KernelAlgExec#OperationId
  override type Process = KernelAlgExec#OperationId

  implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool)

  private val kernel: KernelAlgExec = new KernelAlgExec {}


  override def signal(): Signal = kernel.createBroadcast()

  override def await(s: KernelAlgExec#OperationId)(continuation: PartialFunction[Any, Unit]): Unit = kernel.registerToBroadcast(s, continuation)

  override def createProcess(processAction: PartialFunction[Any, Unit]): UUID = kernel.spawnOperation(processAction)

  override def startProcess(process: Process): Unit = kernel.startOperation(process)

  /**
    * Creating a channel create a "callback" channel.
    *
    * @param channel input channal
    * @param data    send data
    * @return the resulting channel
    */
  override def emit(channel: UUID, data: Any): Unit = {
    kernel.broadcastMessage(channel, data)
    kernel.execAll()
  }

  override def debug(): Unit = kernel.debug()
}
