package hpi.datamining.sca.mise.wrapper.sources

import java.net.ServerSocket

import rx.lang.scala.subjects.ReplaySubject
import scala.io.BufferedSource

class SocketSource(port: Int) extends MISESource {

  val server = new ServerSocket(port)
  val data = ReplaySubject[(Double, Double)]()

  new Thread {
    override def run(): Unit = {
      obtainDataFromServer(server)
    }
  }.start()

  def parseLines(lines: TraversableOnce[String]): TraversableOnce[(Double, Double)] = {
    lines
      .map(_.split(",").map(_.trim))
      .map { case Array(x, y) => (x.toDouble, y.toDouble) }
  }

  def obtainDataFromServer(s: ServerSocket): Unit = {
    val s = server.accept()
    val lines = new BufferedSource(s.getInputStream).getLines()
    val parsedLines = parseLines(lines)
    parsedLines.foreach(data.onNext)
  }

  override def foreach(f: ((Double, Double)) => Unit): Unit = data.foreach(f)

  override def hasDefiniteSize: Boolean = false

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new ObservableSource(data.filter(f))
}
