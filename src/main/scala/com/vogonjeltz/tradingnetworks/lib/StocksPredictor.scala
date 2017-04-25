package com.vogonjeltz.tradingnetworks.lib

import breeze.stats.distributions.Rand

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * StocksPicker
  *
  * Created by fredd
  */
abstract class StocksPredictor(
  protected var _stock: StockHistory,
  val choiceThreshold: Double = 0.02
){

  def stock: StockHistory = _stock

  def predict(): Double

  def nextSignal(s: Double): Unit

  def predictedScore: Int = 0

  def test(nextStockDays: StockHistory):List[Double] = {

    var cash = 100d
    val plOverTime = ListBuffer[Double](cash)

    var numOwned = 0d
    var dayBefore = nextStockDays.openings.head

    for (day <- nextStockDays.openings) {

      //val prediction = predict()
      val prediction = Random.nextInt(day.toInt * 2) - day

      //val sign =  if (prediction > day * (1 + choiceThreshold)) 1
      //            else if (prediction < day * (1 - choiceThreshold)) -1
      //            else 0

      val sign = Random.nextInt(3) - 1

      //println(s"I own $numOwned")

      //Do trade
      if (sign == 1) {
        //If currently in SHORT position then close that and buy
        if (numOwned <= 0) {
          cash += numOwned * day
          numOwned = cash / day
          cash = 0
        }

      } else if (sign == -1) {

        if (numOwned >= 0) {
          cash += numOwned * day
          numOwned = -cash / day
          cash += -numOwned * day
        }

      } else {
        //Close all positions to reduce exposure
        cash += numOwned * day
        numOwned = 0
      }

      nextSignal(day)

      plOverTime.append(cash + numOwned * day)

      dayBefore = day

    }

    cash += numOwned * dayBefore
    plOverTime.append(cash)

    plOverTime.toList

  }

}
