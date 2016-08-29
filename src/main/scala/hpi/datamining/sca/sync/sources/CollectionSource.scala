package hpi.datamining.sca.sync.sources

class CollectionSource(col: TraversableOnce[(Double, Double)]) extends MISESource {
  override def foreach(f: ((Double, Double)) => Unit): Unit = col.foreach(f)

  override def hasDefiniteSize: Boolean = col.hasDefiniteSize

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new CollectionSource(col.filter(f))
}
