import scala.collection.JavaConverters._

def fromJava: java.util.Map[String, java.lang.Integer] = {
  val map = new java.util.HashMap[String, java.lang.Integer]()
  map.put("key", null)
  map
}

// watch out here...Integer is not Int!
val map = fromJava.asScala.asInstanceOf[scala.collection.Map[String, Int]]
println(map("key") == null)
println(map("key") == 0)

val doubles: Seq[Double] = Seq(1.0, Double.NaN, 1.1)
println(doubles.max)
val doubles1: Seq[Double] = Seq(1.0, 1.1, Double.NaN)
println(doubles1.max)

val (x, y) = (List(1, 3, 5), List(2, 4, 6)).zipped find (_._1 > 10) getOrElse 10
//Console println s"Found $x"