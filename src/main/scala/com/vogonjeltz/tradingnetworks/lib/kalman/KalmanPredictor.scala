package com.vogonjeltz.tradingnetworks.lib.kalman

import com.vogonjeltz.tradingnetworks.lib.{StockHistory, StocksPredictor}

/**
  * KalmanPredictor
  *
  * Created by fredd
  */
class KalmanPredictor(
  initialStock: StockHistory,
  Q_searchspace: List[Double] = (1 to 49).map(_/100d).toList ::: (10 to 20).map(_/20d).toList,
  R_searchspace: List[Double] = (1 to 100).map(_/100d).toList,
  ct: Double = 0.02
) extends StocksPredictor(initialStock, ct) {

  val stocksOptimizer = new StocksOptimizer(Q_searchspace.toIndexedSeq, R_searchspace.toIndexedSeq, initialStock, 10, choiceThreshold)
  var (q,r,_ps) = stocksOptimizer.optimize()

  override def predictedScore: Int = _ps

  val filter: StocksFilter = new StocksFilter(q,r,stock)
  filter.train()

  override def predict(): Double = filter.predict().x

  override def nextSignal(s: Double): Unit = {
    filter.update(s)

  }

}
