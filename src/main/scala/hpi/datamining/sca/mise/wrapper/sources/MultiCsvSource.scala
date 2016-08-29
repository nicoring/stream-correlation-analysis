package hpi.datamining.sca.mise.wrapper.sources

import scala.collection.mutable

object MultiCsvSource {

  def loadLines(filePath: String) = io.Source.fromFile(filePath).getLines()

  def getHeaders(filePaths: Seq[String], delimiter: String): Seq[Array[String]] = {
    filePaths.map(loadLines).map(_.next().split(delimiter).map(_.trim))
  }

  def getColumnNums(headers: Seq[Array[String]], columnNames: Seq[String]): Seq[Int] = {
    headers zip columnNames map { case (header, columnName) => header.indexOf(columnName)}
  }

  /** Select Columns by their name; without an index
    *
    * @param filePaths
    * @param columnNames
    * @param delimiter
    * @return
    */
  def byColumnName(filePaths: Seq[String],
            columnNames: Seq[String],
            delimiter: String = ","): MultiCsvSource = {
    val headers = getHeaders(filePaths, delimiter)
    val columnNums = getColumnNums(headers, columnNames)
    new MultiCsvSource(filePaths, columnNums, Seq(), delimiter, hasHeader = true, hasIndex = false)
  }

  /** Select Columns by their name; with an index
    *
    * @param filePaths
    * @param columnNames
    * @param indexColumnNames
    * @param delimiter
    * @return
    */
  def byColumnNameWithIndex(filePaths: Seq[String],
            columnNames: Seq[String],
            indexColumnNames: Seq[String],
            delimiter: String = ","): MultiCsvSource = {
    val headers = getHeaders(filePaths, delimiter)
    val columnNums = getColumnNums(headers, columnNames)
    val indexNums = getColumnNums(headers, indexColumnNames)
    new MultiCsvSource(filePaths, columnNums, indexNums, delimiter, hasHeader = true, hasIndex = true)
  }

  /** Select columns by their number; without an index
    *
    * @param filePaths
    * @param columnNums
    * @param delimiter
    * @param hasHeader
    * @return
    */
  def byColumnNumber(filePaths: Seq[String],
            columnNums: Seq[Int],
            delimiter: String = ",",
            hasHeader: Boolean = false): MultiCsvSource =
    new MultiCsvSource(filePaths, columnNums, Seq(), delimiter, hasHeader, hasIndex = false)

  /** Select columns by their number; with an index
    *
    * @param filePaths
    * @param columnNums
    * @param indexColumns
    * @param delimiter
    * @param hasHeader
    * @return
    */
  def byColumnNumberWithIndex(filePaths: Seq[String],
                              columnNums: Seq[Int],
                              indexColumns: Seq[Int],
                              delimiter: String = ",",
                              hasHeader: Boolean = false): MultiCsvSource =
    new MultiCsvSource(filePaths, columnNums, indexColumns, delimiter, hasHeader, hasIndex = true)

}

class MultiCsvSource(filePaths: Seq[String],
                     columnNums: Seq[Int],
                     indexColumns: Seq[Int],
                     delimiter: String = ",",
                     hasHeader: Boolean = false,
                     hasIndex: Boolean = false) extends MISESource {
  require(filePaths.size == 2, "Currently only two values are possible, got " + filePaths.size)
  import MultiCsvSource._

  val parsedPairs = loadPairs()

  def joinOnIndex(left: (Integer, Iterator[Array[String]]), right: (Integer, Iterator[Array[String]])):
    Seq[(Array[String], Array[String])] = {

    def leftIndex(arr: Array[String]) = arr(left._1).toInt
    def rightIndex(arr: Array[String]) = arr(right._1).toInt

    val leftIter = left._2
    val rightIter = right._2

    val buf = mutable.Buffer[(Array[String], Array[String])]()

    def getNext[T](iterator: Iterator[T]): Option[T] = if (iterator.hasNext) Some(iterator.next()) else None

    var currentLeft = getNext(leftIter)
    var currentRight = getNext(rightIter)

    while (currentLeft.isDefined && currentRight.isDefined) {
      val indLeft = leftIndex(currentLeft.get)
      val indRight = rightIndex(currentRight.get)

      if (indLeft == indRight) {
        buf += ((currentLeft.get, currentRight.get))
        currentLeft = getNext(leftIter)
        currentRight = getNext(rightIter)
      } else if (indLeft < indRight) {
        currentLeft = getNext(leftIter)
      } else if (indLeft > indRight) {
        currentRight = getNext(rightIter)
      } else {
        throw new IllegalStateException("Should not happen!")
      }
    }

    buf
  }


  def loadPairs(): TraversableOnce[(Double, Double)] = {
    var lines = filePaths.map(loadLines).map(_.map(_.split(delimiter).map(_.trim)))
    if (hasHeader)
      lines = lines.map(_.drop(1))

    val Seq(iter1, iter2) = lines
//    val pairs = iter1 zip iter2

    val num1 = columnNums(0)
    val num2 = columnNums(1)

    if (hasIndex) {
      val joined = joinOnIndex((indexColumns(0), iter1), (indexColumns(1), iter2))
      for ((arr1, arr2) <- joined) yield (arr1(num1).toDouble, arr2(num2).toDouble)
    } else {
      for ((arr1, arr2) <- iter1 zip iter2) yield (arr1(num1).toDouble, arr2(num2).toDouble)
    }
//    for {
//      (entries1, entries2) <- pairs
//      if !hasIndex || entries1(indexColumns(0)) == entries2(indexColumns(1))
//    } yield (entries1(columnNums(0)).toDouble, entries2(columnNums(1)).toDouble)

  }

  override def foreach(f: ((Double, Double)) => Unit): Unit = parsedPairs.foreach(f)

  override def hasDefiniteSize: Boolean = true

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new CollectionSource(parsedPairs.filter(f))
}
