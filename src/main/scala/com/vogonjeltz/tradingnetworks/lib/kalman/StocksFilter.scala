package com.vogonjeltz.tradingnetworks.lib.kalman

import com.vogonjeltz.tradingnetworks.app.StockHistory

import scala.collection.mutable.ListBuffer

/**
  * StocksFilter
  *
  * Created by fredd
  */
class StocksFilter(Q: Double, R: Double, trainingData: StockHistory) extends KalmanFilter(Q, R, trainingData.openings.head, 1) with FilterTracking {

  def train() = {
    for (day <- trainingData.openings.drop(1)) {
      tick(day)
    }
  }

  def predictDay(truePrice: Double): Double = {
    val price = state.x
    tick(truePrice)
    price
  }

  def test(testData: StockHistory, investAmount: Double = 100): List[Double] = {
    var dayBefore = testData.openings.head
    var pl = investAmount
    val plOverTime: ListBuffer[Double] = ListBuffer()

    tick(dayBefore)
    for (day <- testData.openings.drop(1)) {
      val prediction = predictDay(day)
      if (prediction > dayBefore) {
        val numBought = investAmount / dayBefore
        val profit = (day - dayBefore) * numBought
        pl += profit
      } else if (prediction < dayBefore) {
        val numBought = investAmount / dayBefore
        val profit = (dayBefore - day) * numBought
        pl += profit
      }
      plOverTime.append(pl)
      dayBefore = day
    }
    plOverTime.toList
  }

}
