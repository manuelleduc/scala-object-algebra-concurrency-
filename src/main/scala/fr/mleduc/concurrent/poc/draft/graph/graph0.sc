import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

object graph0 {

  val g = Graph[Int, DiEdge]()

  g add (1 ~> 2)
  g add (3 ~> 2)


  g.get(2).diPredecessors.foreach((x: g.NodeT) => {
    g.add((x.value + 10) ~> 2)
    g.remove(x)
  })
  g.remove(2)
  /*predecessors.foreach((x: Int) => {
    g.remove(x ~> 2)
  })*/

  g.add(3 ~> 11)

  println(g)


  g.nodes.foreach((x: g.NodeT) => {
    println(s"${x.value} --> ${g.get(x).diSuccessors.size}")
  })


}