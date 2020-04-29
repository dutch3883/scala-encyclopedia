package com.dutch.learning

object Exercise8 {
  trait IntSet {
    def incl(x: Int): IntSet
    def contains(x: Int): Boolean
    def union(x: IntSet): IntSet
    def intersect(x: IntSet): IntSet
  }
  case object Empty extends IntSet {
    def contains(x: Int): Boolean = false
    def incl(x: Int): IntSet = Set(Empty, x, Empty)

    override def union(x: IntSet): IntSet = x

    override def intersect(x: IntSet): IntSet = this
  }
  case class Set(left: IntSet, elem: Int, right: IntSet) extends IntSet {
    def contains(x: Int): Boolean =
      if (x < elem) left contains x
      else if (x > elem) right contains x
      else true

    def incl(x: Int): IntSet =
      if (x < elem) Set(left incl x, elem, right)
      else if (x > elem) Set(left, elem, right incl x)
      else this

    override def union(x: IntSet): IntSet = right.union(left.union(x.incl(elem)))

    override def intersect(x: IntSet): IntSet =
      if(x.contains(elem)) Set(left.intersect(x),elem,right.intersect(x))
      else left.intersect(x).union(right.intersect(x))
  }

  def main(args: Array[String]): Unit = {
    val set1 = Empty.incl(1).incl(2).incl(5).incl(8).incl(12).incl(16).incl(20)
    val set2 = Empty.incl(1).incl(2).incl(7).incl(10).incl(12).incl(13).incl(20)

    println(set1)
    println(set2)
    println(set1.union(set2))
    println(set1.intersect(set2))

  }
}
