//implicit val z1 = 2
def addTo(n: Int) = {
  def add(x: Int)(y: Int)(implicit z: Int) = x + y + z
  add(n) _
}

implicit val z2 = 3
val addTo1 = addTo(1)
val r = addTo1(2)
println(r)
//addTo1(2)(3)

def invert(v3: Int)(v2: Int = 2, v1: Int = 1) {
  println(v1 + ", " + v2 + ", " + v3)
}
def invert3: (Int, Int) => Unit = invert(3) _

//invert3(v1 = 2)
invert3(v1 = 2, v2 = 1)