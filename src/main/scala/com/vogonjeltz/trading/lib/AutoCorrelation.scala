package com.vogonjeltz.trading.lib

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by Freddie on 08/05/2017.
  */
object AutoCorrelation{

  def calcCoefficient(data:Array[Double], k: Int = 1): Double = {

    val average = data.sum / data.length

    //println(s"k -> $k")
    //println(data.indices.take(data.length - k + 1).length - data.length)

    var sum1: Double = 0
    for (i <- data.indices.take(data.length - k)) {
      sum1 += (data(i) - average) * (data(i + k) - average)
    }

    var sum2: Double = 0
    for (d <- data) {
      sum2 += Math.pow(d - average, 2)
    }

    sum1 / sum2

  }

  def predict(coefficients: Array[Double], data: Array[Double], order: Int) = {

    //http://www.itl.nist.gov/div898/handbook/pmc/section4/pmc444.htm

    assert(coefficients.length <= data.length)

    val delta = (1 - coefficients.take(order).sum) * (data.sum / data.length)

    var sum: Double = delta

    for (c <- coefficients.drop(1).zip(data.reverse.take(coefficients.length - 1))) {
      sum += c._1 * c._2
    }

    sum

  }

  def getCoeffs(data: Array[Double], n: Int = 20):Array[Double] = {

    val coeffs: ArrayBuffer[Double] = ArrayBuffer()
    for (i <- Range(0, n)) {
      coeffs.append(calcCoefficient(data, n))
    }

    coeffs.toArray

  }

}
