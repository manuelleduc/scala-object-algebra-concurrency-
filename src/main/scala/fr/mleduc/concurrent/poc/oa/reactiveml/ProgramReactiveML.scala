package fr.mleduc.concurrent.poc.oa.reactiveml

/**
  * Created by mleduc on 02/11/16.
  */
object ProgramReactiveML extends App {

  def program2(alg: ReactiveMLAlg) = {

    import alg._


    val instantaneousLoop: PartialFunction[Int, Unit] = {
      case x: Int => (1 to x).foreach((z) => println(z * 100))
    }


    val kill = signal()
    val nonInstantaneousLoop = createProcess({
      case x: Int =>
        forLoop(1, x, (x: Int) => {
          println(x)
        }, Some(kill))
    })


    parallel({ case _ => startProcess(nonInstantaneousLoop, 10) }, { case _ => instantaneousLoop(10) })
  }

  def program1(alg: ReactiveMLAlg) = {

    import alg._

    val s = signal()

    val p = createProcess {
      case _ =>
        await(s) {
          case z => println(s"Hello $z")
        }
    }

    startProcess(p)

    emit(s, "abc")

    debug()
  }

  private val algExec = new ReactiveMLAlgExec {}
  program1(algExec)
  program2(algExec)
}
