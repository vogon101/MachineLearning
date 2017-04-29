package com.vogonjeltz.tradingnetworks.lib

/**
  * StockHistory
  *
  * Created by fredd
  */
class StockHistory (val name: String, val data: List[(String, Double, Double)]) extends Iterable[(String, Double, Double)] {

  def length = data.length

  override def iterator = data.iterator

  lazy val openings: List[Double] = data.map(_._2)
  lazy val closings: List[Double] = data.map(_._3)
  lazy val days: List[String] = data.map(_._1)

  override def drop(n: Int) = new StockHistory(name, data.drop(n))
  override def take(n: Int) = new StockHistory(name, data.take(n))
  def get(n: Int):(String, Double, Double) = data(n)

  override def head: (String, Double, Double) = get(0)
  override def last: (String, Double, Double) = data.last

  override def tail: StockHistory = new StockHistory(name, data.tail)

  def + (d: String, o: Double, c: Double): StockHistory =
    new StockHistory(name, data :+ (d, o, c))

}
