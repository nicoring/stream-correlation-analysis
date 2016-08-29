package hpi.datamining.sca.mise.wrapper.sources

import rx.lang.scala.Observable

class ObservableSource(observable: Observable[(Double, Double)]) extends MISESource {

  override def foreach(f: ((Double, Double)) => Unit): Unit = observable.foreach(f)

  override def hasDefiniteSize: Boolean = false

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new ObservableSource(observable.filter(f))
}
