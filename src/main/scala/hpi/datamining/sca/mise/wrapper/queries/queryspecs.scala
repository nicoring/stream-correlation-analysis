package hpi.datamining.sca.mise.wrapper.queries

import dmf.stream.mutinf.{EstimationResult, WindowSpec}

trait EmbeddedQuerySpec {
  def windowSpec: WindowSpec
}

case class TriggerQuerySpec(windowSpec: WindowSpec,
                            predicate: (EstimationResult) => Boolean,
                            onTrigger: (EstimationResult) => Unit) extends EmbeddedQuerySpec

case class ContinuousQuerySpec(windowSpec: WindowSpec) extends EmbeddedQuerySpec