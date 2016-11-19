object A {
  def test() = {
    var x = 0
    lazy val y = 1 / x // have to be extracted in a function, otherwise evaluated by the sheet too early
    try {
      println(y)
    } catch {
      case _: Throwable =>
        x = 1
        println(y)
    }
  }
}

A.test()