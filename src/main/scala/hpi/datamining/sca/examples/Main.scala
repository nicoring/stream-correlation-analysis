package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Transformation

object Main {
  def main(args: Array[String]) {
    val source = new IntSource
    val t1 = new SquareTransformation
    val t2 = new ToStringTransformation
    val sink = new PrintSink[Any]

    val base = source
      .via(t2)

    val squared = source
      .via(t1)
      .via(t2)

//    squared.to(sink)
//    base.to(sink)
    source.to(sink)
    source.to(sink)

//    Transformation.merge(squared, base)
//      .transform { case (s1, s2) => s1 + "," + s2 }
//      .to(sink)
  }
}
