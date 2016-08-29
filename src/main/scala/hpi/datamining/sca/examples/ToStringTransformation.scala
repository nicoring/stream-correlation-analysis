package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Transformation
import rx.lang.scala.Observable

class ToStringTransformation extends Transformation[Int, String] {
  override protected def transform(observable: Observable[Int]): Observable[String] = observable.map(_.toString)
}
