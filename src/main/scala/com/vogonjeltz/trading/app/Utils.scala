package com.vogonjeltz.trading.app

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.StockHistory

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by Freddie on 03/04/2017.
  */
object Utils {

  def readStock(code: String): StockHistory = {
    //println(s"Reading stock $code")
    val bufferedSource = io.Source.fromFile(s"data/$code.csv")
    //(Date, Open, Close)
    val stockHistory: ListBuffer[(String, Double, Double)] = new ListBuffer[(String, Double, Double)]()
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      stockHistory.append((
        cols(0), cols(1).toDouble, cols(4).toDouble
      ))
    }
    bufferedSource.close()
    new StockHistory(code, stockHistory.toList)
  }

  def readMinst(offset: Int = 0, n: Int = 0): List[(Int, DenseVector[Double])] = {

    val bufferedSource = io.Source.fromFile("data/mnist.csv")

    val data: ArrayBuffer[(Int, DenseVector[Double])] = ArrayBuffer()

    val iterator = bufferedSource.getLines().drop(offset)

    for (line <- if (n == 0) iterator else iterator.take(n)) {
      val cols = line.split(",").map(_.trim)

      val label = cols(0).toInt
      val vect = ArrayBuffer[Double]()
      for (i <- Range(0, 28*28)) {
        //TODO: Check that this loads correct data
        vect.append(
          (cols(i + 1).toDouble/255) * 2 - 1d
        )
      }

      data.append((label, DenseVector[Double](vect.toArray)))

    }

    bufferedSource.close()

    data.toList

  }

  def writeText(text: String, path: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(path))
    pw.write(text)
    pw.close()
  }

  def readText(path: String): String = {
    val s = io.Source.fromFile(path)
    s.getLines().toList.mkString("\n")
  }

}
