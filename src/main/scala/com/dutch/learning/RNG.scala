package com.dutch.learning

import scala.annotation.tailrec

trait RNG {
  def nextInt: (Int,RNG)
}

object RNG {
  type Rand[+A] = RNG => (A,RNG)
  val int: Rand[Int] = _.nextInt
  val doubleR: Rand[Double] = double(_)
  def unit[A](a:A):Rand[A] = rng => (a,rng)
  def map[A,B](s:Rand[A])(f:A => B): Rand[B] = rng => {
    val (a,rng2) = s(rng)
    (f(a),rng2)
  }
  def flatMap[A,B](f:Rand[A])(g: A => Rand[B]): Rand[B] = rng => {
    val result = f(rng)
    g(result._1)(result._2)
  }

  def nonNegativeLessThan(n:Int): Rand[Int] = {
    flatMap[Int,Int](nonNegativeInt)(i => {
      val mod = i % n
      if(i + (n-1) - mod >= 0) unit(mod) else nonNegativeLessThan(n)
    })
  }


  def map2[A,B,C](rand1:Rand[A],rand2:Rand[B])(f:(A,B) => C):Rand[C] = rng => {
    val (a,rng1) = rand1(rng)
    val (b,rng2) = rand2(rng1)
    (f(a,b),rng2)
  }

  def double(rng:RNG): (Double,RNG) = {
    val result = nonNegativeInt(rng)
    (result._1.toDouble/Int.MaxValue, result._2)
  }

  def intDouble(rng:RNG): ((Int,Double), RNG) = {
    val result1 = int(rng)
    val result2 = double(result1._2)
    ((result1._1,result2._1),result2._2)
  }

  def doubleInt(rng:RNG): ((Double,Int), RNG) = {
    val result1 = int(rng)
    val result2 = double(result1._2)
    ((result2._1,result1._1),result2._2)
  }

  def both[A,B](ra:Rand[A], rb:Rand[B]): Rand[(A,B)] = {
    map2(ra,rb)((_,_))
  }

  def ints(count:Int)(rng:RNG): (List[Int],RNG) = sequence(List.fill(count)(RNG.int):_*)(rng)
  def nonNegativeInt(rng:RNG): (Int, RNG) = map(_.nextInt)({
    case x if x >= 0 => x
    case x if x < 0 => x * -1
    case Int.MinValue => Int.MaxValue
  })(rng)

  def sequence[A](fs: Rand[A]*): Rand[List[A]] = {
    @tailrec def inSeq(resultList:List[A],l:List[Rand[A]])(rng:RNG): (List[A],RNG) = {
      val newResult = l.head(rng)
      l.tail match {
        case List() => (resultList ++ List(newResult._1),newResult._2)
        case a => inSeq(resultList ++ List(newResult._1),a)(newResult._2)
      }
    }
    inSeq(List(),fs.toList)
  }
}

case class SimpleRNG(seed:Long) extends  RNG {

  override def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRNG = SimpleRNG(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }

}