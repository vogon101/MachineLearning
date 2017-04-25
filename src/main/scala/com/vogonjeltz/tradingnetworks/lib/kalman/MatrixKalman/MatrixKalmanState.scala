package com.vogonjeltz.tradingnetworks.lib.kalman.MatrixKalman

import breeze.linalg.{DenseMatrix, DenseVector, diag}

/**
  * Created by Freddie on 25/04/2017.
  */
class MatrixKalmanState(val Q: DenseMatrix[Double], val R: DenseMatrix[Double], val x: DenseVector[Double], val p: DenseMatrix[Double])
{
  def this(q: Double, r: Double, x: DenseVector[Double], p: Double) = {
    this(
      diag(DenseVector(Array.fill(x.length)(q))),
      diag(DenseVector(Array.fill(x.length)(r))),
      x,
      diag(DenseVector(Array.fill(x.length)(p)))
    )
  }

  def from_state(new_x: DenseVector[Double], new_p: DenseMatrix[Double]): MatrixKalmanState = new MatrixKalmanState(Q,R, new_x, new_p)

  val H: DenseMatrix[Double] = eye

  def variables = x.length
  def eye = DenseMatrix.eye[Double](variables)

}
