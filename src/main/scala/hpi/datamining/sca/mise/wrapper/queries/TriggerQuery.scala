package hpi.datamining.sca.mise.wrapper.queries

import dmf.stream.mutinf.{EstimationResult, EstimatorStream}
import hpi.datamining.sca.mise.wrapper.TriggerQuerySpec

class TriggerQuery(mise: EstimatorStream, spec: TriggerQuerySpec) extends EmbeddedQuery(mise, spec) {
  override def handleQueryResult(result: EstimationResult): Unit = {
    if (spec.predicate(result)) {
      spec.onTrigger(result)
    }
  }
}
