package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, EstimatorStream, EstimatorStreamMISEv3Factory, WindowSpec}

trait SamplingTechnique {
  def tag: String
}
case object ReservoirSampling extends SamplingTechnique {
  override def tag: String = "RS"
}
case object SlidingWindowSampling extends SamplingTechnique {
  override def tag: String = "SW"
}
case object IncrementalReciprocalReservoirSampling extends SamplingTechnique {
  override def tag: String = "MS_F"
}
case object IncrementalReciprocalReservoirSamplingOptimized extends SamplingTechnique {
  override def tag: String = "MS_D"
}

object ReferenceMISE {

  /** Possible sampling techniques provided by [dmf.stream.mutinf.EstimatorStreamMISEv3Factory]
    *
    * RS: Traditional reservoir sampling gives a UNIFORM distribution over time
    * SW: Sliding window sampling gives a BOX distribution over time
    * MS_F: Incremental reciprocal sampling, but with fixed sample size (based on optimized version)
    * MS_D: Same as before, but optimized implementation
    */
  val possibleSamplingTechniques = Seq("RS", "SW", "MS_F", "MS_D")

  def apply(k: Int, alpha: Int, samplingTechnique: SamplingTechnique = ReservoirSampling): ReferenceMISE = {
    new ReferenceMISE(k, alpha, samplingTechnique.tag)
  }
}

class ReferenceMISE(k: Int, alpha: Int, sampleTechnique: String = "RS") extends EstimatorStream {
  import ReferenceMISE._

  require(possibleSamplingTechniques contains sampleTechnique,
    s"samplingTechnique must be one of $possibleSamplingTechniques, but got: $sampleTechnique")

  val factory = new EstimatorStreamMISEv3Factory(k, sampleTechnique, alpha)
  val mise = factory.build()

  override def addData(x: Double, y: Double): Unit = mise.addData(x, y)

  override def length: Long = mise.length

  override def miQueryImpl(windowSpec: WindowSpec): EstimationResult = {
    val res = mise.miQuery(windowSpec)
    EstimationResult(res.mi, res.statSize)
  }

  override val referenceImplementation: Boolean = mise.referenceImplementation
  override val minPossibleQuerySize: Int = mise.minPossibleQuerySize
}
