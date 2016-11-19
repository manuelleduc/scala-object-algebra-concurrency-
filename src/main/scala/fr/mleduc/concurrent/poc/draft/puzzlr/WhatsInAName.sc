class C {
  def sum(x: Int = 1, y: Int = 2): Int = x + y
}
class D extends C {
  override def sum(y: Int = 3, x: Int = 4): Int = super.sum(x, y)
}
val d: D = new D
val c: C = d
c.sum(x = 0)
d.sum(x = 0)