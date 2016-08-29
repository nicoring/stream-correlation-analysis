package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, WindowSpec}
import hpi.datamining.sca.sync.sources.{CollectionSource, MISESource, ObservableSource}
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MISEController {

  def length: Long

  def addData(x: Double, y: Double): Unit

  def query(numDrop: Long, numTake: Long): Future[EstimationResult] = query(WindowSpec(numDrop, numTake))

  def query(windowSpec: WindowSpec): Future[EstimationResult]

  def rangeQuery(start: Int, end: Int, numStep: Int, numTake: Int): Future[Seq[EstimationResult]] = {
    rangeQuery(start to end by numStep, numTake)
  }

  def rangeQuery(range: Range, numTake: Int): Future[Seq[EstimationResult]] = {
    val results = for (numDrop <- range) yield query(numDrop, numTake)
    Future.sequence(results.reverse)
  }

  def rangeQueryWithIndex(start: Int, end: Int, numStep: Int, numTake: Int): Future[Seq[(Long, EstimationResult)]] = {
    rangeQueryWithIndex(start to end by numStep, numTake)
  }

  def rangeQueryWithIndex(range: Range, numTake: Int): Future[Seq[(Long, EstimationResult)]] = {
    def getIndex(numDrop: Int): Long = length - (numDrop + numTake)
    val results = for (numDrop <- range) yield query(numDrop, numTake)
    val indices =  for (numDrop <- range) yield getIndex(numDrop)
    Future.sequence(results).map { res => (indices zip res).reverse }
  }

  def addContinuousQuery(windowSpec: WindowSpec): Observable[EstimationResult]

  def addTriggerQuery(windowSpec: WindowSpec,
                      predicate: EstimationResult => Boolean,
                      onTrigger: EstimationResult => Unit): Unit

  def addDataFrom(source: MISESource): Unit = source.foreach { case (x, y) => addData(x, y) }

  def addDataFrom(collection: TraversableOnce[(Double, Double)]): Unit = addDataFrom(new CollectionSource(collection))

  def addDataFrom(observable: Observable[(Double, Double)]): Unit = addDataFrom(new ObservableSource(observable))

  def startInteractiveMode(): Unit = {
    println("Start interactive mode")
    for (ln <- io.Source.stdin.getLines) {
      val elems = ln.split(" ").map(_.trim)
      elems.head match {
        case "length" => println(length)
        case "insert" =>
          val Array(_, x, y) = elems
          addData(x.toDouble, y.toDouble)
        case "query" =>
          val Array(_, numDrop, numTake) = elems
          val w = WindowSpec(numDrop.toLong, numTake.toLong)
          query(w).foreach(println)
        case "quit" =>
          return
        case x => println("Don't know command: " + x)
      }
    }
  }
}
