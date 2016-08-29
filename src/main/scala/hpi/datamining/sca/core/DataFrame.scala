package hpi.datamining.sca.core

trait DataFrame[T] {
  def getHeader: DataHeader
  def get(attributeName: String): T
  def get(index: Int): T
  def apply(index: Int) = get(index)
}
