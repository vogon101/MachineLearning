package com.vogonjeltz.tradingnetworks.lib.networks

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 29/03/2017.
  */
case class Vect(data: List[Double]) {

  val length: Int = data.length
  def average = data.sum / data.length
  def diff(that: Vect):Double = {
    assert(this.length == that.length)
    var acc = 0d
    for ((a,b) <- this.data.zip(that.data)) acc += Math.abs(a-b)
    acc
  }

  def apply(n: Int): Double = data(n)

  def fill(n: Int, x: Double) = {
    if (data.length < n) new Vect(data ::: List.fill(n - data.length)(0d))
    else this
  }

  override def toString = "Vect(" + data.mkString(",") + ")"

}

object Vect {

  def apply(as: Double*) = new Vect(as.toList)

  def ZERO(n: Int) = new Vect(List.fill(n)(0))

  def ofDim(n: Int)(f: (Int) => Double) = {
    val buf = new ListBuffer[Double]()
    for (i <- 0 until(n)) buf.append(f(i))
    Vect(buf.toList)
  }

}
