package com.vogonjeltz.tradingnetworks.lib.kalman

import com.vogonjeltz.tradingnetworks.lib.StockHistory

/**
  * StocksOptimizer
  *
  * Created by fredd
  */
class StocksOptimizer(
   val Q_searchSpace: IndexedSeq[Double],
   val R_searchSpace: IndexedSeq[Double],
   val stock: StockHistory,
   val trainingDays: Int,
   choiceThreshold: Double = 0.05
) {

  /*
  def this(Q_ss: List[Double], R_ss: List[Double], stockHistory: StockHistory, trainingDays: Int) =
    this(Q_ss.toIndexedSeq, R_ss.toIndexedSeq, stockHistory, trainingDays, 0.05)

  /**
    *
    * @return (q, r, pl)
    */
  def optimize():(Double, Double, Int) ={

    var bestq, bestr = 0d
    var bestScore = -10000

    for (q <- Q_searchSpace) {
      for (r <- R_searchSpace) {

        val filter = new StocksFilter(q, r, stock.take(20))
        filter.train()
        val pl = evaluate(filter, stock.drop(20))
        if (pl > bestScore) {
          bestq = q
          bestr = r
          bestScore = pl
        }

      }
    }

    (bestq, bestr, bestScore)

  }

  def evaluate(filter: StocksFilter, testData: StockHistory): Int = {

    var score = 0
    var dayBefore = testData.openings.head
    for (day <- testData.openings.drop(1)) {
      val prediction = filter.predict().x
      if (prediction > dayBefore * (1 + choiceThreshold)) {
        if (day > dayBefore) score += 1
        else score -= 1
      } else if (prediction < dayBefore * (1 - choiceThreshold)) {
        if (day < dayBefore) score +=1
        else score -= 1
      }
    }

    score
  }
  */

}
