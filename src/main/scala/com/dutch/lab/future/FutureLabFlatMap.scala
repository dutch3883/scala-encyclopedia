package com.dutch.lab.future
import scala.concurrent.{Future,Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object FutureLabFlatMap {


  def main(args: Array[String]): Unit = {
    var globalIncrement = ""
    val startTime = System.currentTimeMillis()
    val future1 = Future {Thread.sleep(4200); globalIncrement += "[future1]"}
    val future2 = Future {Thread.sleep(4200); globalIncrement += "[future2]"}
//    val combindF = future1.flatMap(_ => future2)
    val combindF = Future.sequence(Seq(future1,future2))
    Await.result(combindF,20 seconds)
    val endTime = System.currentTimeMillis()
    print(s"Result: $globalIncrement Time used: ${endTime - startTime} ms")
  }
}
