package hpi.datamining.sca.sync.queries

import dmf.stream.mutinf.{EstimationResult, EstimatorStream}
import hpi.datamining.sca.sync.EmbeddedQuerySpec

abstract class EmbeddedQuery(mise: EstimatorStream, spec: EmbeddedQuerySpec) {
  val id: String = java.util.UUID.randomUUID.toString

  def name: String = getClass.getSimpleName + "-" + id

  def handleNewData() = {
    val result = mise.miQuery(spec.windowSpec)
    handleQueryResult(result)
  }

  def handleQueryResult(result: EstimationResult)
}
