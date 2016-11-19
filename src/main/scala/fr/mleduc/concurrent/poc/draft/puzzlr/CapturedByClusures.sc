import scala.collection.mutable

object CapturedByClosures {
  val funcs1 = mutable.Buffer[() => Int]()
  val funcs2 = mutable.Buffer[() => Int]()

  {
    val values = Seq(100, 110, 120)
    var j = 0
    for (i <- values.indices) {
      funcs1 += (() => values(i))
      val tmp: Int = values(j)
      funcs2 += (() => tmp)
      j += 1
    }
  }

  funcs1 foreach { f1 => println(f1()) }
  funcs2 foreach { f2 => println(f2()) }
}