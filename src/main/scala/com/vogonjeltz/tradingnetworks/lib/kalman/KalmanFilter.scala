package com.vogonjeltz.tradingnetworks.lib.kalman

import scala.collection.mutable.ListBuffer

/**
  * KalmanFilter
  *
  * Created by fredd
  */
class KalmanFilter (val predictionStep: (KalmanState) => KalmanState, protected var _state: KalmanState) {

  def state: KalmanState = _state

  def predict(): KalmanState = {
    _state = predictionStep(state)
    state
  }

  def update(signal: Double): KalmanState = {
    val xi = state.x
    val pi = state.p

    val K = pi / (pi + state.R)
    val xk = xi + K * (signal - xi)
    val pk = (1 - K) * pi

    _state = state.nextTick(xk, pk)

    state
  }

  def tick(signal: Double): KalmanState = {
    predict()
    update(signal)
  }

}

object KalmanFilter {

  def apply(prediction: (KalmanState) => KalmanState)(state: KalmanState):KalmanFilter =
    new KalmanFilter(prediction, state)

}

/**
  * Kalman filter that uses simple time based prediction, effectively no model
  */
class SimplePredictionKalmanFilter (initialSate: KalmanState)
  extends KalmanFilter(SimplePredictionKalmanFilter.simplePredictionFunction, initialSate)

object SimplePredictionKalmanFilter {

  val simplePredictionFunction: (KalmanState) => KalmanState = (s) => s.nextTick(s.x, s.p + s.Q)

}

trait FilterTracking extends KalmanFilter {

  private val _xs: ListBuffer[Double] = ListBuffer()
  private val _ps: ListBuffer[Double] = ListBuffer()

  override def update(z: Double): KalmanState = {
    _xs.append(state.x)
    _ps.append(state.p)
    super.update(z)
    state
  }

  def xs = _xs.toList
  def ps = _ps.toList

}
