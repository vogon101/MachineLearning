package com.vogonjeltz.tradingnetworks.lib

/**
  * StockHistory
  *
  * Created by fredd
  */
class StockHistory (val data: List[(String, Double, Double)]) extends Iterable[(String, Double, Double)] {

  def length = data.length

  override def iterator = data.iterator

  lazy val openings: List[Double] = data.map(_._2)
  lazy val closings: List[Double] = data.map(_._3)
  lazy val days: List[String] = data.map(_._1)

  override def drop(n: Int) = new StockHistory(data.drop(n))
  override def take(n: Int) = new StockHistory(data.take(n))

  def + (d: String, o: Double, c: Double): StockHistory =
    new StockHistory(data :+ (d, o, c))

}
