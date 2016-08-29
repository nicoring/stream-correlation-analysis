package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, WindowSpec}

//case class EstimationResult(mi: Double, statSize: Long, pos: Long)

trait MIEstimator {
  def addData(x: Double, y: Double): Unit
  def query(windowSpec: WindowSpec): EstimationResult
  def length: Long
}