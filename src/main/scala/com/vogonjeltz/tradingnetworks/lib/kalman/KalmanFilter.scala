package com.vogonjeltz.tradingnetworks.lib.kalman

import scala.collection.mutable.ListBuffer

/**
  * KalmanFilter
  *
  * Created by fredd
  */
class KalmanFilter (private var _state: KalmanState) {

  def this(q: Double, r: Double, initialX: Double, initialP: Double) = this(new KalmanState(q, r, initialX, initialP))

  def state = _state

  def tick(z: Double): KalmanState = {

    val xi = state.x
    val pi = state.p + state.Q

    val K = pi / (pi + state.R)
    val xk = xi + K * (z - xi)
    val pk = (1 - K) * pi

    _state = state.nextTick(xk, pk)
    state

  }

}

trait FilterTracking extends KalmanFilter {

  private val _xs: ListBuffer[Double] = ListBuffer()
  private val _ps: ListBuffer[Double] = ListBuffer()

  override def tick(z: Double): KalmanState = {
    super.tick(z)
    _xs.append(state.x)
    _ps.append(state.p)
    state
  }

  def xs = _xs.toList
  def ps = _ps.toList

}
