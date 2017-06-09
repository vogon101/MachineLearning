package com.vogonjeltz.machineInt.lib.models.movingaverage

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 27/05/2017.
  */
class WeightedMovingAverage (val period: Int){

  val weights: List[Double] = Range(1, period + 1).map(_.toDouble).toList

  private var _data: ListBuffer[Double] = ListBuffer()
  private var _x = 0d

  def window: List[Double] = _data.toList
  def x = _x

  def update(i: Double): Double = {
    _data.append(i)
    if (window.length > period) {
      _data = _data.drop(1)
      _x = window
        .zip(weights)
        .map(X => X._1 * X._2)
        .sum / weights.sum
    } else {
      _x = window
        .zip(weights.drop(period -  window.length))
        .map(X => X._1 * X._2)
        .sum / weights.drop(period -  window.length).sum
    }

    _x
  }

}
