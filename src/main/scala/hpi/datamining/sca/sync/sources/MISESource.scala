package hpi.datamining.sca.sync.sources

import scala.collection.mutable

trait MISESource {
  def foreach(f: ((Double, Double)) => Unit): Unit
  def hasDefiniteSize: Boolean
  def filter(f: ((Double, Double)) => Boolean): MISESource
  def collect(): Seq[(Double, Double)] = {
    if (!hasDefiniteSize) {
      throw new UnsupportedOperationException("Cannot collect elements of a Source without a definite size.")
    }
    val buf = mutable.Buffer[(Double, Double)]()
    for (elem <- this) buf += elem
    buf
  }
}
