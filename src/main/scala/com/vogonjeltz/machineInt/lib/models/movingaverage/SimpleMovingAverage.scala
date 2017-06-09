package com.vogonjeltz.machineInt.lib.models.movingaverage

import com.vogonjeltz.machineInt.lib.StockHistory

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 26/05/2017.
  */
class SimpleMovingAverage (val period: Int) {

  private var _data: ListBuffer[Double] = ListBuffer()
  private var _x = 0d

  def window: List[Double] = _data.toList
  def x = _x

  def update(i: Double): Double = {
    _data.append(i)
    assert(_data.last == i)
    if(window.length > period) _data = _data.drop(1)
    _x = window.sum / window.length
    _x
  }

}
