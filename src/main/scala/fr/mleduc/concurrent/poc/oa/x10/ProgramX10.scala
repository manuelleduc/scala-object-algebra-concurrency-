package fr.mleduc.concurrent.poc.oa.x10

import scala.util.Random

/**
  * Created by mleduc on 09/11/16.
  */
object ProgramX10 extends App {

  def program1(alg: X10Alg): Int = {
    val n = 10
    val p = 20

    var result = 0

    alg.finish({
      (1 to p).foreach(_ => {
        alg.async({
          val rand = new Random()
          var myResult = 0d

          (1 to (n / p)).foreach(_ => {
            val x = rand.nextDouble()
            val y = rand.nextDouble()
            if (x * x + y * y <= 1) myResult += 1
          })

          alg.atomic({
            result += myResult
          })
        })


      })
    })

    val pi: Int = 4 * result / n
    pi
  }

  //program1(new X10AlgExec {})

}
