class A (val a: Int, val b: Int)

object A {

  def apply(a: Int, b: Int) = new A(a,b)

  def unapply(arg: A): Option[(Int, Int)] = Some(arg.a, arg.b)

}


val obj = A(10,3)

println (obj match {
  case A(10, b) => b
  case A(2, _) => "Hello"
  case _ => "Nah"
})
