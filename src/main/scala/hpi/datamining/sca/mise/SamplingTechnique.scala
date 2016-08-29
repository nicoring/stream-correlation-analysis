package hpi.datamining.sca.mise

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
