package fr.mleduc.concurrent.poc.draft.programmingscala

/**
  * Created by mleduc on 27/10/16.
  */
trait Logger {
  def log(message: String): Unit
}

class ConsoleLogger extends Logger {
  override def log(message: String): Unit = println(s"log: $message")
}

trait Service {
  type Log <: Logger
  val logger: Log
}

class Service1 extends Service {
  type Log = ConsoleLogger

  val logger: ConsoleLogger = new ConsoleLogger
}