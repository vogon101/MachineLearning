package com.vogonjeltz.tradingnetworks.lib.utils

import breeze.linalg.DenseMatrix

/**
  * Created by Freddie on 25/04/2017.
  */
object MatrixUtils {

  def eye(n: Int) = DenseMatrix.eye[Double](n)

}
