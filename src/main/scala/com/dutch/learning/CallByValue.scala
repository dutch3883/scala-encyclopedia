package com.dutch.learning

object CallByValue {
  def testLazyParam(in: => String): Unit =  {
    lazy val j = in
    println(s"do it")
    println(s"do it $j")
    println(s"do it again $j\n")
  }

  def testCallByValueParam(j: => String): Unit =  {
    println(s"do it")
    println(s"do it $j")
    println(s"do it again $j\n")
  }

  def testEagerParam(j:String): Unit =  {
    println(s"do it")
    println(s"do it $j")
    println(s"do it again $j\n")
  }

  def testFunctionParam(j:() => String): Unit =  {
    println(s"do it")
    println(s"do it ${j()}")
    println(s"do it again ${j()}\n")

  }

  def main(args: Array[String]): Unit = {
    testCallByValueParam({println("call by value creating... "); "hello world"})
    testLazyParam({println("lazy creating... "); "hello world"})
    testEagerParam({println("eager creating... "); "hello world"})
    testFunctionParam(() => {println("function creating... "); "hello world"})
  }

}
