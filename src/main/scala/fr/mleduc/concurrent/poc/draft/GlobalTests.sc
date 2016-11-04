import fr.mleduc.concurrent.poc.{Language, LanguageImpl}

def program1(alg: Language) = {
  import alg._

  println("start")

  val sA = signal()
  bind(sA, (sig: Signal) => (x: Any) => {
    case x: Int => send(sig, x + 1)
  })
  bind(sA, (sig: Signal) => (x: Any) => {
    case x: String => send(sig, x + 1)
  })

  val res = send(sA, 1)

  bind(res, (sig: Signal) => (x: Any) => {
    case x => println(x)
  })


}


program1(new LanguageImpl {})