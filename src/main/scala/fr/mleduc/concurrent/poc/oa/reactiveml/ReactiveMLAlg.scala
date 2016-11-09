package fr.mleduc.concurrent.poc.oa.reactiveml

import java.util.UUID
import java.util.concurrent.Executors

import fr.mleduc.concurrent.poc.oa.kernel.KernelAlgExec

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mleduc on 02/11/16.
  */
trait ReactiveMLAlg {
  type Signal
  type Process

  def debug()

  def parallel(fct1: PartialFunction[Any, Unit], fct2: PartialFunction[Any, Unit])

  def signal(): Signal

  def await(s: Signal)(continuation: PartialFunction[Any, Unit])

  def createProcess(processAction: PartialFunction[Any, Unit]): Process

  def startProcess(process: Process, value: Any = Unit): Unit

  /**
    * Creating a channel create a "callback" channel.
    *
    * @param channel input channal
    * @param data    send data
    * @return the resulting channel
    */
  def emit(channel: Signal, data: Any): Unit

  def forLoop(start: Int, stop: Int, function: (Int) => Unit, until: Option[Signal] = None)
}

trait ReactiveMLAlgExec extends ReactiveMLAlg {
  override type Signal = KernelAlgExec#OperationId
  override type Process = KernelAlgExec#OperationId

  implicit val exec = ExecutionContext.fromExecutor(Executors.newCachedThreadPool)

  private val kernel: KernelAlgExec = new KernelAlgExec {}

  override def debug(): Unit = kernel.debug()

  override def forLoop(start: Int, stop: Int, function: (Int) => Unit, until: Option[UUID]): Unit = {
    def loop(itt: Int): Unit = {
      if (itt <= stop) {
        val internalChannel = this.signal()
        val internalProcess = this.createProcess({
          case _ => this.await(internalChannel) {
            case x: Int =>
              function(x)
              loop(x + 1)
          }
        })
        this.startProcess(internalProcess)
        this.emit(internalChannel, itt)
      }
    }

    loop(start)
  }

  override def signal(): Signal = kernel.createBroadcast()

  override def await(s: KernelAlgExec#OperationId)(continuation: PartialFunction[Any, Unit]): Unit = kernel.registerToBroadcast(s, continuation)

  override def createProcess(processAction: PartialFunction[Any, Unit]): UUID = kernel.spawnOperation(processAction)

  override def startProcess(process: Process, value: Any = Unit): Unit = kernel.startOperation(process, value)

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

  override def parallel(fct1: PartialFunction[Any, Unit], fct2: PartialFunction[Any, Unit]): Unit = {
    Future(fct1())
    Future(fct2())
  }
}
