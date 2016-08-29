package hpi.datamining.sca.mise.wrapper.sources

object CsvSource {

  val defaultParseFunc = positionParseFunc(0, 1) _

  def loadLines(filePath: String) = io.Source.fromFile(filePath).getLines()

  def positionParseFunc(pos1: Int, pos2: Int)(lineElems: Array[String]): (Double, Double) = {
    (lineElems(pos1).toDouble, lineElems(pos2).toDouble)
  }

  def parseColumns(columnNames: Seq[String], filePath: String, delimiter: String = ","): CsvSource = {
    require(columnNames.size == 2, "Currently only two values are possible, got " + columnNames.size)
    val header = loadLines(filePath).next().split(delimiter)
    val indices = columnNames.map(header.indexOf)
    val parseFunc = positionParseFunc(indices(0), indices(1)) _
    new CsvSource(filePath, parseFunc, delimiter, hasHeader = true)
  }
}

class CsvSource(filePath: String,
                parseFunc: Array[String] => (Double, Double) = CsvSource.defaultParseFunc,
                delimiter: String = ",",
                hasHeader: Boolean = false
               ) extends MISESource {
  import CsvSource._

  def lines = loadLines(filePath)
  val numDrop = if (hasHeader) 1 else 0
  val parsedLines: Iterator[(Double, Double)] = parseLines(lines.drop(numDrop))

  def parseLines(lines: Iterator[String]): Iterator[(Double, Double)] = {
    lines
      .map(_.split(delimiter))
      .map(parseFunc)
  }

  override def foreach(f: ((Double, Double)) => Unit): Unit = parsedLines.foreach(f)

  override def hasDefiniteSize: Boolean = true

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new CollectionSource(parsedLines.filter(f))
}
