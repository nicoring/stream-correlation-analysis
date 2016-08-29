package hpi.datamining.sca.mise

import dmf.stream.mutinf.WindowSpec
import org.scalatest.FunSpec

class MiseImplTest extends FunSpec {
  describe("MISE implementation from mise.jar") {



    it("should answer the queries as described on the website") {

      for (st <- ReferenceMISE.possibleSamplingTechniques) {
        println()
        println("sampling technique: " + st)

        val mise = new ReferenceMISE(3, 10, st)

        def consumeData(data: Seq[(Double, Double)]) = {
          for ((x, y) <- data) mise.addData(x, y)
        }

        val data1 = Seq(
          (2.3, 1.9),
          (4.2, 4.3),
          (0.3, 6.2),
          (8.1, 1.4),
          (2.9, 4.1)
        )

        val data2 = Seq(
          (2.6, 2.6),
          (2.2, 1.8),
          (5.1, 3.5),
          (4.1, 2.4),
          (3.2, 1.3)
        )

        val data3 = Seq(
          (8.8, 6.4),
          (5.5, 6.8),
          (4.2, 7.2),
          (7.4, 4.1),
          (9.6, 3.1)
        )

        val w1 = WindowSpec(0, 5)
        val w2 = WindowSpec(5, 5)
        val w3 = WindowSpec(0, 15)

        consumeData(data1)
        println(mise.miQuery(w1))
        consumeData(data2)
        println(mise.miQuery(w1))
        consumeData(data3)
        println(mise.miQuery(w1))
        println(mise.miQuery(w2))
        println(mise.miQuery(w3))
        println(mise.length)
      }
    }
  }
}
