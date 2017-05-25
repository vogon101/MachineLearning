package com.vogonjeltz.trading.app


import breeze.linalg.DenseVector
import breeze.plot._
import com.vogonjeltz.trading.lib.{AutoCorrelation, StockHistory}
import com.vogonjeltz.trading.lib.kalman.{KalmanFilter, KalmanPredictor, KalmanState}

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 25/04/2017.
  */
object MatrixTest extends App {

  val q = 0.01
  val r = 0.99

  val STOCKS = List("III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP","ATVI","ADBE","AKAM","ALXN","GOOG","GOOGL","AMZN","AAL","AMGN","ADI","AAPL","AMAT","ADSK","ADP","BIDU","BIIB","BMRN","AVGO","CA","CELG","CERN","CHTR","CHKP","CTAS","CSCO","CTXS","CTSH","CMCSA","COST","CSX","CTRP","XRAY","DISCA","DISCK","DISH","DLTR","EBAY","EA","EXPE","ESRX","FB","FAST","FISV","GILD","HAS","HSIC","HOLX","IDXX","ILMN","INCY","INTC","INTU","ISRG","JBHT","JD","KLAC","LRCX","LBTYA","LBTYK","LILA","LILAK","LVNTA","QVCA","MAT","MXIM","MCHP","MU","MSFT","MDLZ","MNST","MYL","NTES","NFLX","NCLH","NVDA","ORLY","PCAR","PAYX","PYPL","QCOM","REGN","ROST","SHPG","SIRI","SWKS","SBUX","SYMC","TMUS","TSLA","TXN","KHC","PCLN","TSCO","TRIP","FOXA","ULTA","VRSK","VRTX","VIAB","VOD","WBA","WDC","XLNX","YHOO")
  val stockData = STOCKS.map(X => Utils.readStock(X))

  val cleverDeltas = ListBuffer[Double]()
  val stupidDeltas = ListBuffer[Double]()

  val finalCPLs = ListBuffer[Double]()
  val averageCErrors = ListBuffer[Double]()

  val finalDPLs = ListBuffer[Double]()
  val averageDErrors = ListBuffer[Double]()

  for (stock <- stockData.take(50) ) {

    println(stock.name)

    val filterD = new KalmanPredictor(0.01, 0.7, stock.take(20), false)
    val (plD, errorD) = filterD.evaluate(stock.drop(20), 0)

    val filterC = new KalmanPredictor(0.01, 0.7, stock.take(20), true)
    val (plC, errorC) = filterC.evaluate(stock.drop(20), 0)


    /*
    val fig = Figure()
    val plot_0 = fig.subplot(0)
    //plot_0 += plot(DenseVector(errorC.indices.map(_.toDouble).toArray), DenseVector(errorC.toArray.map(X => Math.abs(X))))
    plot_0 += plot(DenseVector(plC.indices.map(_.toDouble).toArray), DenseVector(plC.toArray))
    plot_0 += plot(DenseVector(plD.indices.map(_.toDouble).toArray), DenseVector(plD.toArray))

    val plot_1 = fig.subplot(2,1,1)
    plot_1 += plot(DenseVector(stock.openings.indices.map(_.toDouble).toArray), DenseVector(stock.openings.toArray))
    plot_1 += plot(DenseVector(filterC.predictedValues.indices.map(_.toDouble).toArray), DenseVector(filterC.predictedValues.toArray))
    plot_1 += plot(DenseVector(filterC.smoothedValues.indices.map(_.toDouble).toArray), DenseVector(filterC.smoothedValues.map(_(0)).toArray))


    val coeffs: ListBuffer[Double] = ListBuffer()
    for (i <- Range(0, 20)) {
      coeffs.append(AutoCorrelation.calcCoefficient(filterC.residuals.toArray, i))
    }

    val plot_2 = fig.subplot(3,1,2)
    //plot_2 += plot(DenseVector(coeffs.indices.map(_.toDouble).toArray), DenseVector(coeffs.toArray), '+')
    //plot_2.ylim(-1,1)
    plot_2 += plot(DenseVector(errorC.indices.map(_.toDouble).toArray), DenseVector(errorC.toArray.map(X => Math.abs(X))))
    plot_2 += plot(DenseVector(errorD.indices.map(_.toDouble).toArray), DenseVector(errorD.toArray.map(X => Math.abs(X))))
    */


    finalDPLs.append(plD.last - 100)
    averageDErrors.append(errorD.map(Math.abs(_) * 100).sum / errorD.length)

    finalCPLs.append(plC.last - 100)
    averageCErrors.append(errorC.map(Math.abs(_) * 100).sum / errorC.length)

  }

  val deltaImprovements = cleverDeltas.toList.zip(stupidDeltas.toList).map(X => ((X._2 - X._1) / X._1)* 100)

  val fig = Figure()
  val plot_0 = fig.subplot(0)
  plot_0 += plot(DenseVector(finalCPLs.indices.map(_.toDouble).toArray), DenseVector(finalCPLs.zipWithIndex.map(X => X._1 - finalDPLs(X._2)).toArray))
  //plot_0 += plot(DenseVector(finalDPLs.indices.map(_.toDouble).toArray), DenseVector(finalDPLs.toArray))


  val plot_1 = fig.subplot(2,1,1)
  plot_1 += plot(DenseVector(averageCErrors.indices.map(_.toDouble).toArray), DenseVector(averageCErrors.toArray))
  plot_1 += plot(DenseVector(averageDErrors.indices.map(_.toDouble).toArray), DenseVector(averageDErrors.toArray))
  plot_1.ylim(0, 30)

  println(s"Average clever profits: ${finalCPLs.sum / finalCPLs.length}")
  println(s"Average dumb profits: ${finalDPLs.sum / finalDPLs.length}")

  /*
  plot_0 += plot(DenseVector(stock.openings.indices.map(_.toDouble).toArray), DenseVector(stock.openings.toArray))
    plot_0 += plot(DenseVector(filter.smoothedValues.indices.map(_.toDouble).toArray), DenseVector(filter.smoothedValues.map(_(0)).toArray))
    plot_0 += plot(DenseVector(filter.predictedValues.indices.map(_.toDouble).toArray), DenseVector(filter.predictedValues.toArray))


    val plot_1 = fig.subplot(2,1,1)
    //plot_1 += plot(DenseVector(stock.closings.indices.map(_.toDouble).toArray), DenseVector(stock.closings.toArray))
    plot_1 += plot(DenseVector(filter.smoothedValues.indices.map(_.toDouble).toArray), DenseVector(filter.residuals.toArray))
    plot_1 += plot(DenseVector(filter.smoothedValues.indices.map(_.toDouble).toArray), DenseVector(filter.predictedResiduals.toArray))


    val mCleverDeltas = ListBuffer[Double]()
    val mStupidDeltas = ListBuffer[Double]()
    for (i <- filter.predictedValues.indices) {
      val cleverDelta = ((filter.predictedValues(i) - stock.openings(i)) / stock.openings(i)) * 100
      val stupidDelta = ((filter.predictedSmoothedQuadratic(i) - stock.openings(i)) / stock.openings(i)) * 100

      mCleverDeltas.append(cleverDelta)
      mStupidDeltas.append(stupidDelta)
    }

    val plot_2 = fig.subplot(3,1,2)
    plot_2 += plot(DenseVector(mCleverDeltas.indices.map(_.toDouble).toArray), DenseVector(mCleverDeltas.toArray))
    plot_2 += plot(DenseVector(mStupidDeltas.indices.map(_.toDouble).toArray), DenseVector(mStupidDeltas.toArray))
    plot_2 += plot(DenseVector(mCleverDeltas.indices.map(_.toDouble).toArray), DenseVector(Array.fill(mCleverDeltas.length)(0d)))

    val cAvg = mCleverDeltas.map(_.abs).sum / mCleverDeltas.length
    val sAvg = mStupidDeltas.map(_.abs).sum / mStupidDeltas.length

    println(s"Stock: ${stock.name}")
    println("Average Clever Delta: " + cAvg)
    println("Average Stupid Delta: " + sAvg)
    println("Average Improvement: " + ((sAvg - cAvg) / sAvg) * 100)

    cleverDeltas.append(cAvg)
    stupidDeltas.append(sAvg)
   */

}
