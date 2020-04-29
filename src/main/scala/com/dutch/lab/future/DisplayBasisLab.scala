package com.dutch.lab.future

object DisplayBasisLab {

  trait MathOperation[A] {
    protected case class GetSet(get:getNum,set:setNum)
    private implicit class GetterSetter(obj:A){
      def set(value:Double)(implicit getset:GetSet): A =getset.set(value,obj)
      def get(implicit getset:GetSet): Double =getset.get(obj)
    }
    type setNum = (Double,A) => A
    type getNum = A => Double
    protected def self:A = this.asInstanceOf[A]
    protected def gs:List[GetSet]

    protected def operateAllNum(fs: List[A => A]): A = fs.foldLeft(identity[A] _)(_ compose _)(self)
    protected def generalOps[B](reassign:(A,GetSet) => A)(another:B): A = operateAllNum(gs.map(lgs => reassign(_,lgs)))

    private def plus(base:A,another:A)(implicit localGetSet:GetSet):A = base.set(base.get + another.get)
    def +(another:A):A = generalOps[A](plus(_,another)(_))(another)
    private def plusN(base:A,another:Double)(implicit localGetSet:GetSet):A = base.set(base.get + another)
    def +(another:Double):A = generalOps[Double](plusN(_,another)(_))(another)

    private def subtract(base:A,another:A)(implicit localGetSet:GetSet):A = base.set(base.get - another.get)
    def -(another:A):A = generalOps[A](subtract(_,another)(_))(another)

    private def multiply(base:A,another:Double)(implicit localGetSet:GetSet):A = base.set(base.get * another)
    def *(multiplier:Double):A = generalOps[Double](multiply(_,multiplier)(_))(multiplier)

    private def divide(base:A,another:Double)(implicit localGetSet:GetSet):A = base.set(base.get / another)
    def /(divider:Double):A = generalOps[Double](divide(_,divider)(_))(divider)

    private def powerN(base:A,degree:Double)(implicit localGetSet:GetSet):A = base.set( scala.math.pow(base.get,degree) )
    def ^(power:Double):A = generalOps[Double](powerN(_,power)(_))(power)
  }

  case class DisplayPrice(exclusive: Double, allInclusive: Double) extends MathOperation[DisplayPrice] {
    override def gs: List[GetSet] = List(
      GetSet(_.allInclusive,(d,in) => in.copy(allInclusive = d)),
      GetSet(_.exclusive,(d,in) => in.copy(exclusive = d)),
    )
  }

  case class DisplayBasis(perBook:DisplayPrice, perPack:DisplayPrice) extends MathOperation[DisplayBasis] {
    override def gs: List[GetSet] = {
      List(
        GetSet(_.perBook.allInclusive, (d, in) => in.copy(perBook = in.perBook.copy(allInclusive = d))),
        GetSet(_.perBook.exclusive, (d, in) => in.copy(perBook = in.perBook.copy(exclusive = d))),
        GetSet(_.perPack.allInclusive, (d, in) => in.copy(perPack = in.perPack.copy(allInclusive = d))),
        GetSet(_.perPack.exclusive, (d, in) => in.copy(perPack = in.perPack.copy(exclusive = d))),
      )
    }
  }


  def main(args: Array[String]): Unit = {
  }
}
