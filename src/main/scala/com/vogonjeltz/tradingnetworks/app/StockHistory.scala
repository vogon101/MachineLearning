package com.vogonjeltz.tradingnetworks.app

/**
  * StockHistory
  *
  * Created by fredd
  */
class StockHistory (val data: List[(String, Double, Double)]) {

  def last = data.last
  def head = data.head

  lazy val openings: List[Double] = data.map(_._2)
  lazy val closings: List[Double] = data.map(_._3)
  lazy val days: List[String] = data.map(_._1)

  def drop(n: Int) = new StockHistory(data.drop(n))
  def take(n: Int) = new StockHistory(data.take(n))

}
