package com.vogonjeltz.tradingnetworks.app

import breeze.linalg.DenseVector
import breeze.plot._
import com.vogonjeltz.tradingnetworks.lib.StockHistory
import com.vogonjeltz.tradingnetworks.lib.kalman.MatrixKalman.{MatrixFilter, MatrixKalmanState}

/**
  * Created by Freddie on 25/04/2017.
  */
object MatrixTest extends App {

  val q = 0.1
  val r = 0.1

  val STOCKS = List("AAPL")
  val stockData = STOCKS.map(X => new StockHistory(Utils.readStock(X)))

  for (stock <- stockData) {
    val initialState = new MatrixKalmanState(q, r, DenseVector(stock.openings.head), 0d)

    val filter = new MatrixFilter(initialState)

    for (day <- stock.openings.drop(1)) {

      filter.predict()
      filter.update(DenseVector(day))

    }

    val fig = Figure()
    val plot_0 = fig.subplot(0)
    plot_0 += plot(DenseVector(stock.openings.indices.map(_.toDouble).toArray), DenseVector(stock.openings.toArray))
    plot_0 += plot(DenseVector(filter.xs.indices.map(_.toDouble).toArray), DenseVector(filter.xs.map(_(0)).toArray))


  }

}
