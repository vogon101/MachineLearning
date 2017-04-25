package com.vogonjeltz.tradingnetworks.lib.kalman

import com.vogonjeltz.tradingnetworks.lib.StockHistory

import scala.collection.mutable.ListBuffer

/**
  * StocksFilter
  *
  * Created by fredd
  */
class StocksFilter(Q: Double, R: Double, trainingData: StockHistory)
  extends SimplePredictionKalmanFilter(KalmanState(Q, R, trainingData.openings.head, 1)) with FilterTracking {

  def train(): Unit = {
    for (day <- trainingData.openings.drop(1)) {
      tick(day)
    }
  }

}
