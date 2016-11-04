package fr.mleduc.concurrent.poc.oa.reactiveml

/**
  * Created by mleduc on 02/11/16.
  */
object Program extends App {

  def program1(alg: ReactiveMLAlg) = {

    val s = alg.signal()

    val p = alg createProcess ({
      case _ =>
        alg.await(s) {
          case z => println(s"Hello $z")
        }
    }: PartialFunction[Any, Unit])

    alg.startProcess(p)

    //alg.emit(s, ())
    alg.emit(s, "abc")

    alg.debug()
  }

  program1(new ReactiveMLAlgExec {})
}
