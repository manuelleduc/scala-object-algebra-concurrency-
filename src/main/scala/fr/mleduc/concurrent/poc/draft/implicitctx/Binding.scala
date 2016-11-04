package fr.mleduc.concurrent.poc.draft.implicitctx

object Paper0 {

  /**
    * Created by mleduc on 28/10/16.
    */
  trait Binding[E] {
    def lambda(x: String, b: E): E

    def vari(x: String): E

    def apply(e1: E, e2: E): E

    def let(x: String, e: E, b: E): E
  }

  type Val = Int
  type EvE = Env => Val
  type Env = Map[String, Val]

  /*class Clos(x:String, b:EvE, e:Env) extends Val {
    def apply(v:Val):Val = b(e + (x -> v))
  }*/

  trait EVEBinding extends Binding[EvE] {
    //override def lambda(x: String, b: EvE): EvE = env => new Clos(x,b,env)

    override def vari(x: String): EvE = env => env(x)

    //override def apply(e1: EvE, e2: EvE): EvE = env => e1(env).apply(e2(env))

    override def let(x: String, e: EvE, b: EvE): EvE = env => b(env + (x -> e(env)))
  }
}