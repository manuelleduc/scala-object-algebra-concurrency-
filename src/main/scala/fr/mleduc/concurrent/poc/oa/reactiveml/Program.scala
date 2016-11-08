package fr.mleduc.concurrent.poc.oa.reactiveml

/**
  * Created by mleduc on 02/11/16.
  */
object Program extends App {

  def program2(alg: ReactiveMLAlg) = {
    val instantaneousLoop: PartialFunction[Int, Unit] = {
      case x: Int => (1 to x).foreach((z) => println(z * 100))
    }


    val kill = alg.signal()
    val nonInstantaneousLoop = alg.createProcess({
      case x: Int =>
        alg.forLoop(1, x, (x: Int) => {
          println(x)
        }, Some(kill))
    })


    alg.parallel({ case _ => alg.startProcess(nonInstantaneousLoop, 10) }, { case _ => instantaneousLoop(10) })
  }

  def program1(alg: ReactiveMLAlg) = {

    val s = alg.signal()

    val p = alg createProcess {
      case _ =>
        (alg await s) {
          case z => println(s"Hello $z")
        }
    }

    alg.startProcess(p)

    alg.emit(s, "abc")

    alg.debug()
  }

  program1(new ReactiveMLAlgExec {})
  program2(new ReactiveMLAlgExec {})
  println("end")
}
