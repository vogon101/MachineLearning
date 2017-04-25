package com.vogonjeltz.tradingnetworks.lib.kalman

/**
  * KalmanState
  *
  * Created by fredd
  */
class KalmanState(val Q: Double, val R: Double, val x: Double, val p: Double) {


  def nextTick(n_x: Double, n_p: Double) = new KalmanState(Q, R, n_x, n_p)

}

object KalmanState {
  def apply(q: Double, r: Double, x: Double, p: Double) = new KalmanState(q,r,x,p)
}
