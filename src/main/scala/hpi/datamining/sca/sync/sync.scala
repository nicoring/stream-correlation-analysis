package hpi.datamining.sca

import dmf.stream.mutinf.{EstimationResult, WindowSpec}

package object sync {

  trait EmbeddedQuerySpec {
    def windowSpec: WindowSpec
  }

  case class TriggerQuerySpec(windowSpec: WindowSpec,
                              predicate: (EstimationResult) => Boolean,
                              onTrigger: (EstimationResult) => Unit) extends EmbeddedQuerySpec

  case class ContinuousQuerySpec(windowSpec: WindowSpec) extends EmbeddedQuerySpec
}