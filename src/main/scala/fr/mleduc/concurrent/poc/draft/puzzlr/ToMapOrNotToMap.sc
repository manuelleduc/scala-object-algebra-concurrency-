case class RomanNumeral(symbol: String, value: Int)

implicit object RomanOrdering extends Ordering[RomanNumeral] {
  def compare(a: RomanNumeral, b: RomanNumeral) = a.value compare b.value
}

import scala.collection.immutable.SortedSet

val numerals = SortedSet(RomanNumeral("M", 1000), RomanNumeral("C", 100), RomanNumeral("X", 10), RomanNumeral("I", 1), RomanNumeral("D", 500), RomanNumeral("L", 50), RomanNumeral("V", 5))

println("Roman numeral symbols for 1 5 10 50 100 500 1000:")
for (num <- numerals; sym = num.symbol) {
  print(s"$sym ")
}
numerals map {
  _.symbol
} foreach { sym => print(s"$sym ") }


def value: Int = {
  def one(x: Int): Int = {
    return x
    1
  }
  val two = (x: Int) => {
    return x
    2
  }
  val two1: Int = two(3)
  val r = 1 + one(2) + two1
  println(r)
  r
}

println(value)

implicit val z1 = 2
def addTo(n: Int) = {
  def add(x: Int)(y: Int)(implicit z: Int) = x + y + z
  add(n) _
}

implicit val z2 = 3
val addTo1 = addTo(1)
println(addTo1(2))
//addTo1(2)(3)