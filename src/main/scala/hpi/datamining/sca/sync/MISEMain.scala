package hpi.datamining.sca.sync

import java.io.{BufferedWriter, File, FileWriter}

import dmf.stream.mutinf.WindowSpec
import hpi.datamining.sca.sync.sources.{MultiCsvSource, SocketSource}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object MISEMain {

  def serverSource = new SocketSource(8000)

  def stockDataCsvSource = MultiCsvSource.byColumnNameWithIndex(
    Seq("data/ge.us.txt", "data/ibm.us.txt"),
    Seq("Close", "Close"),
    Seq("Date", "Date")
  )

  /** Writes a sequences of strings to a file */
  def writeToFile(lines: Seq[String], filename: String): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    lines.foreach { line =>
      bw.write(line)
      bw.newLine()
    }
    bw.close()
  }

  def main(args: Array[String]): Unit = {

    val miseController = MISESyncController(4, 1000)
    miseController.addDataFrom(stockDataCsvSource)

    val mis = miseController.rangeQueryWithIndex(0, 10000, 10, 3000)

    mis.onFailure {
      case e => e.printStackTrace()
    }

    val res = Await.result(mis, Duration.Inf).map { case (idx, estRes) => Seq(idx, estRes.mi) }
    val data = stockDataCsvSource.collect()

    val headerStocks = "ge,ibm"
    val headerMi = "idx,mi"

    val stockLines = data.map { case (x,y) => Seq(x,y) mkString "," }
    val miLines = res.map(_ mkString ",")

    writeToFile(headerStocks +: stockLines, "data/ge-ibm-stocks.csv")
    writeToFile(headerMi +: miLines, "data/ge-ibm-mi.csv")

//    miseController.startInteractiveMode()
  }
}
