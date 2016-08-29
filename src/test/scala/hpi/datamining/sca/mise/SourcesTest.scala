package hpi.datamining.sca.mise

import java.net.URL

import hpi.datamining.sca.sync.sources._
import org.scalatest.FunSpec
import rx.lang.scala.Observable

import scala.collection.mutable
import scala.io.Source

class SourcesTest extends FunSpec {

  def collectElements(source: MISESource): Seq[(Double, Double)] = {
    val buf = mutable.Buffer[(Double, Double)]()
    for (elem <- source) buf += elem
    buf
  }

  describe("CollectionSource") {

    it("Should have a definite size") {
      val xs = List()
      val source = new CollectionSource(xs)
      assert(source.hasDefiniteSize)
    }

    it("Should emit correct elements when using foreach") {
      val xs = List(
        (1.0, 1.1),
        (1.1, 3.1),
        (1.5, 4.1),
        (1.0, 4.1),
        (5.0, 1.1)
      )
      val source = new CollectionSource(xs)
      assert(collectElements(source) == xs)
    }
  }

  describe("CsvSource") {

    def parseLines(fileUrl: URL, header: Boolean = false, pos1: Int = 0, pos2: Int = 1): Iterator[(Double, Double)] = {
      val lines = Source.fromURL(fileUrl).getLines()
      if (header) lines.next()
      for (line <- lines) yield {
        val split = line.split(",")
        (split(pos1).toDouble, split(pos2).toDouble)
      }
    }

    describe("CSV with header") {
      it("Should emit correct elements when using foreach") {
        val fileUrl = getClass.getResource("/testInput-header.csv")
        val elems = parseLines(fileUrl, header = true)
        val source = new CsvSource(fileUrl.getPath, hasHeader = true)
        assert(collectElements(source) == elems.toSeq)
      }

      it("Should load correct columns") {
        val fileUrl = getClass.getResource("/testInput-multiColumn.csv")
        val elems = parseLines(fileUrl, header = true, pos1 = 0, pos2 = 2)
        val source = CsvSource.parseColumns(Seq("a", "c"), fileUrl.getPath)
        assert(collectElements(source) == elems.toSeq)
      }
    }

    describe("CSV without header") {
      it("Should emit correct elements when using foreach") {
        val fileUrl = getClass.getResource("/testInput-noheader.csv")
        val elems = parseLines(fileUrl)
        val source = new CsvSource(fileUrl.getPath, hasHeader = false)
        assert(collectElements(source) == elems.toSeq)
      }
    }

  }

  describe("MultiCsvSource") {

    val fileUrls = Seq(
      getClass.getResource("/testMultiCSVInput-1.csv"),
      getClass.getResource("/testMultiCSVInput-2.csv")
    ).map(_.getPath)

    // columns c and y; or 3 and 2
    val expectedNoIndex = Seq(
      (1.0,1.1),
      (1.1,3.1),
      (1.2,5.1),
      (1.3,4.1),
      (1.4,1.1)
    )

    val expectedWithIndex = Seq(
      (1.0,1.1),
      (1.1,3.1),
      (1.2,4.1),
      (1.4,1.1)
    )

    describe("with column name") {
      it("Should emit correct elements when using foreach") {
        val source = MultiCsvSource.byColumnName(fileUrls, Seq("c", "y"))
        assert(collectElements(source) == expectedNoIndex)
      }
    }

    describe("with column name and index") {
      it("Should emit correct elements when using foreach") {
        val source = MultiCsvSource.byColumnNameWithIndex(fileUrls, Seq("c", "y"), Seq("ind", "ind"))
        assert(collectElements(source) == expectedWithIndex)
      }
    }

    describe("with column numbers") {
      it("Should emit correct elements when using foreach") {
        val source = MultiCsvSource.byColumnNumber(fileUrls, Seq(3, 2), hasHeader = true)
        assert(collectElements(source) == expectedNoIndex)
      }
    }

    describe("with column numbers and index") {
      it("Should emit correct elements when using foreach") {
        val source = MultiCsvSource.byColumnNumberWithIndex(fileUrls, Seq(3, 2), Seq(0, 0), hasHeader = true)
        assert(collectElements(source) == expectedWithIndex)
      }
    }
  }

  describe("ObservableSource") {
    it("Should emit correct elements when using foreach") {
      val xs = List(
        (1.0, 1.1),
        (1.1, 3.1),
        (1.5, 4.1),
        (1.0, 4.1),
        (5.0, 1.1)
      )
      val obs = Observable.from(xs)
      val source = new ObservableSource(obs)
      assert(collectElements(source) == xs)
    }
  }
}
