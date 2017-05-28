package com.vogonjeltz.trading.lib.models.movingaverage

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.models.CleverPredictor

/**
  * Created by Freddie on 27/05/2017.
  */
class WMAPredictor (override val period: Int) extends WeightedMovingAverage(period) with CleverPredictor{

  def model_predict() = DenseVector(x)

  def model_update(d: DenseVector[Double]) = {
    DenseVector(update(d(0)))
  }

}