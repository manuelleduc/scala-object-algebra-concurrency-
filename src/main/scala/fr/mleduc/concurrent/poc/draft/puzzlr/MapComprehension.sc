val xs = Seq(Seq("a", "b", "c"), Seq("d", "e", "f"), Seq("g", "h"), Seq("i", "j", "k"))
val ys = for (Seq(x, y, z) <- xs) yield x + y + z
val zs = xs map {
  case Seq(x, y, z) => x + y + z
  case _ => ""
}

object XY {
  object X {
    val value: Int = Y.value + 1
  }
  object Y {
    val value: Int = X.value + 1
  }
}

println(if (math.random > 0.5) XY.X.value else XY.Y.value)