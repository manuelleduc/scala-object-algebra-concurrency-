package fr.mleduc.concurrent.poc.oa.x10

/**
  * Created by mleduc on 09/11/16.
  */
trait X10Alg {
  def finish(operation: Unit)

  def async(operation: Unit)

  def atomic(operation: Unit)
}


trait X10AlgExec extends X10Alg {

}