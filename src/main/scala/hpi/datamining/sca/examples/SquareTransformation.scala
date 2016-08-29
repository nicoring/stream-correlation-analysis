package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Transformation
import rx.lang.scala.Observable

class SquareTransformation extends Transformation[Int, Int] {
  override protected def transform(observable: Observable[Int]): Observable[Int] =
    observable.map(each => each * each)
}
