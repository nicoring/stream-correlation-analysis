package hpi.datamining.sca.mise.wrapper.queries

import dmf.stream.mutinf.{EstimationResult, EstimatorStream}
import hpi.datamining.sca.mise.wrapper.ContinuousQuerySpec
import rx.lang.scala.Subject

class ContinuousQuery(mise: EstimatorStream,
                      val subject: Subject[EstimationResult],
                      spec: ContinuousQuerySpec) extends EmbeddedQuery(mise, spec) {
  override def handleQueryResult(result: EstimationResult): Unit = {
    subject.onNext(result)
  }
}
