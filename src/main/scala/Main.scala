import com.dutch.learning.{RNG, SimpleRNG}

import scala.annotation.tailrec
import scala.io.StdIn

object Main extends App{

	def doubleIntUsage:RNG =>((Int,Double),RNG)= {
		RNG.map2(RNG.int,RNG.double)((a,b) => {
			(a*2,b*3)
		})
	}

	@tailrec def iterate(rng:RNG):Unit = {
		val (result,rng2) = RNG.nonNegativeLessThan(8)(rng)
		println(result)
		StdIn.readLine()
		iterate(rng2)
	}


}
