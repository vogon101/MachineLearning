package com.vogonjeltz.trading.lib.models.movingaverage

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.StockHistory
import com.vogonjeltz.trading.lib.models.CleverPredictor

/**
  * Created by Freddie on 26/05/2017.
  */
class SMAPredictor(override val period: Int) extends SimpleMovingAverage(period) with CleverPredictor{

  def model_predict() = DenseVector(x)

  def model_update(d: DenseVector[Double]) = {
    DenseVector(update(d(0)))
  }

}
