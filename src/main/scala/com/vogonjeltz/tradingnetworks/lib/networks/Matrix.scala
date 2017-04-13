package com.vogonjeltz.tradingnetworks.lib.networks

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 28/03/2017.
  */
sealed trait Matrix {

  val data: Seq[Seq[Double]]

  assert(data.nonEmpty)

  //Assumes symmetrical
  val rows: Int = data.length
  val cols: Int = data.head.length
  val square: Boolean = rows == cols

  def row (n: Int):Seq[Double] = data(n)
  def elem(x: Int)(y: Int): Double = data(x)(y)
  def transform(f: (Double) => Double): Matrix

  override def toString = "Matrix (\n\t" + data.map(R => R.map(C => C.toString).mkString(",")).mkString("\n\t") + "\n)"

}

object Matrix {

  def ofDim(r:Int,c: Int)(f: (Int, Int) => Double):Matrix = {
    val matrix = ListBuffer[List[Double]]()
    val currentRow = ListBuffer[Double]()
    for (x <- 0 until r) {
      for (y <- 0 until c)
        currentRow.append(f(x, y))
      matrix.append(currentRow.toList)
      currentRow.clear()
    }
    new ImmutableMatrix(matrix.toList)
  }

  def ofDim(r: Int)(f: (Int, Int) => Double): Matrix = ofDim(r,r)(f)

  def mutableOfDim(r:Int,c: Int)(f: (Int, Int) => Double):MutableMatrix = {
    val matrix = ListBuffer[ListBuffer[Double]]()
    val currentRow = ListBuffer[Double]()
    for (x <- 0 until r) {
      for (y <- 0 until c)
        currentRow.append(f(x, y))
      matrix.append(currentRow)
      currentRow.clear()
    }
    new MutableMatrix(matrix)
  }

  def mutableOfDim(r: Int)(f: (Int, Int) => Double):MutableMatrix = mutableOfDim(r,r)(f)

}

class ImmutableMatrix (val data: List[List[Double]]) extends Matrix {

  override def transform(f: (Double) => Double) = Matrix.ofDim(rows, cols) ((x, y) => f(data(x)(y)))

}

class MutableMatrix (val data: ListBuffer[ListBuffer[Double]]) extends Matrix {

  override def transform(f: (Double) => Double) = ???


}