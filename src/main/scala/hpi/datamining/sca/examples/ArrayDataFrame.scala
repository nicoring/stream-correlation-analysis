package hpi.datamining.sca.examples

import hpi.datamining.sca.core.{DataFrame, DataHeader}

class ArrayDataFrameFactory[T](header: DataHeader) {
  def create(data: Array[T]) = new ArrayDataFrame[T](data, header)
}

class ArrayDataFrame[T](data: Array[T], header: DataHeader) extends DataFrame[T] {

  override def getHeader: DataHeader = header

  override def get(attributeName: String) = get(header.attributNames.indexOf(attributeName))

  override def get(index: Int): T = data(index)
}
