package fr.mleduc.concurrent.poc.draft.monadreader

import scalaz.Reader

/**
  * Created by mleduc on 27/10/16.
  */
trait Users {

  import scalaz.Reader

  def getUser(id: Int) = Reader((config: Config) =>
    config.userRepository.get(id))

  def findUse(userName: String) = Reader((config: Config) =>
    config.userRepository.find(x => {
      x._1 > userName.length
    })
  )

}

class Config {
  val userRepository: Map[Int, Int] = Map.empty
}

object UserInfo extends Users {
  def userEmail(id:Int) = {
    getUser(id)
  }
}

object TestUsers extends App {
  private val user: Reader[Config, Option[Int]] = UserInfo.getUser(1)
  println(s">>> ${user.run(new Config)}")
}
