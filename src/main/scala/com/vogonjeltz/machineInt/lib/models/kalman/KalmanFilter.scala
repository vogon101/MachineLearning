package com.vogonjeltz.machineInt.lib.models.kalman

import breeze.linalg._

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 25/04/2017.
  */
class KalmanFilter(_initialState: KalmanState){

  private var _state = _initialState
  def state: KalmanState = _state

  //private val _smoothedValues:ListBuffer[DenseVector[Double]] = ListBuffer()
  //def smoothedValues: List[DenseVector[Double]] = _smoothedValues.toList

  def predictKalman():KalmanState = {
    state
  }

  def update(data: DenseVector[Double]): KalmanState = {

    _state = state.from_state(state.x, state.p + state.Q)
    val K = state.p * state.H.t * inv(state.H * state.p * state.H.t + state.R)
    val xn = state.x + K * (data - state.H * state.x)
    val pn = (state.eye - K * state.H) * state.p

    //_smoothedValues.append(xn)

    _state = state.from_state(xn, pn)
    state
  }

}
