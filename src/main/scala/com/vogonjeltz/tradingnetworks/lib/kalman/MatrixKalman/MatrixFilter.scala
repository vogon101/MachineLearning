package com.vogonjeltz.tradingnetworks.lib.kalman.MatrixKalman

import breeze.linalg._
import com.vogonjeltz.tradingnetworks.lib.utils.MatrixUtils._

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 25/04/2017.
  */
class MatrixFilter(_initialState: MatrixKalmanState){

  private var _state = _initialState
  def state = _state

  private val _xs:ListBuffer[DenseVector[Double]] = ListBuffer()
  private val _ps:ListBuffer[DenseMatrix[Double]] = ListBuffer()

  def xs = _xs.toList
  def ps = _ps.toList

  def predict():MatrixKalmanState = {
    _state = state.from_state(state.x, state.p + state.Q)
    state
  }

  def update(data: DenseVector[Double]): MatrixKalmanState = {

    val K = state.p * state.H.t * (state.H * state.p * state.H.t + state.R)
    val xn = state.x + K * (data - state.H * state.x)
    val pn = (state.eye - K * state.H) * state.p


    _xs.append(xn)
    _ps.append(pn)

    _state = state.from_state(xn, pn)
    state
  }

}
