package hpi.datamining.sca.mise

import hpi.datamining.sca.mise.MutualInformationEstimator.DataPoint

case class QueryResult(mcx: Int, mcy: Int, n: Int)

class MarginalPoints

case class Anchor(dataPoint: DataPoint, pos: Long) {
  def query(t1: Long, t2: Long): Option[QueryResult] = ???
  def isInWindow(t1: Long, t2: Long): Boolean = t1 <= pos && t2 <= pos
  def insert(anchor: Anchor) = ???
}

