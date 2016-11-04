package fr.mleduc.concurrent.poc

import fr.mleduc.concurrent.poc.draft.Language


/**
  * Created by mleduc on 26/10/16.
  */
object Programs {


  /*def program1(alg: Language, execCtx: ExecCtx) = {
    import alg._

    /*val operation1 = operation[Int, Int]((x) => x)
    val operation2 = operation[Int, Map[String, String]]((y) => Map.empty)
    operation1()*/

    val param: (alg.Opp[Int]) => alg.Opp[Int] = (x: Opp[Int]) => {
      println(x)
      Thread.sleep(Math.random().toLong)
      x
    }

    val slow: (alg.Opp[Int]) => alg.Opp[Unit] = (x: Opp[Int]) => {
      println("slow1")
      Thread.sleep(10000)
      println("slow2")
      lift(())
    }
    /*parallel(
      finish(parallel(
        seq(opp(param), opp(param)),
        opp(param)
      )),
      opp(slow))*/

    val z: (Int) => alg.Opp[Int] = (x:Int) => lift(x+1)

    //val a: (alg.Param[Int]) => alg.Opp[Int] = (x:Int) => liftParam(x) => lift(x+1)
    //val b: (alg.Param[Int]) => alg.Opp[Int] = ???
   /* parallel(
      finish(
        parallel(
          tmp,
          (x:Int) => lift(Math.pow(x, 2))
        )
      ),
      (x: Int) => lift(x * 2)
    )*/

    //seq((x:String) => lift(x.length), (y:String) => lift(y + "1") )
    //parallel(a,b)

    //parallel(a, parallel(b, c))
  }*/


}


object TestProgram extends App {

  def program1(alg: Language) = {
    import alg._

    println("start")

    val sA = signal()
    val triggerChannel = signal()
    /*bind(sA, (sig: Signal) => (x: Any) => {
      case x: Int => send(sig, triggerChannel, x + 1)
    })
    bind(sA, (sig: Signal) => (x: Any) => {
      case x: String => send(sig, triggerChannel, x + 1)
    })

    val res = send(sA, 1)

    bind(res, (sig: Signal) => (x: Any) => {
      case x => println(x)
    })*/


  }


  //program1(new LanguageImpl {})
}
