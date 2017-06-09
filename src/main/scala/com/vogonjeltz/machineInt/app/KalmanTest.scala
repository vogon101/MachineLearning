package com.vogonjeltz.machineInt.app

import breeze.linalg.DenseVector
import breeze.plot._
import com.vogonjeltz.machineInt.lib.Utils
import com.vogonjeltz.machineInt.lib.models.kalman.KalmanPredictor
import com.vogonjeltz.machineInt.lib.models.movingaverage.{SMAPredictor, WMAPredictor}

import scala.collection.mutable.ListBuffer


/**
  * Created by Freddie on 03/04/2017.
  */
object KalmanTest extends App {

  val STOCKS = List("III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP","ATVI","ADBE","AKAM","ALXN","GOOG","GOOGL","AMZN","AAL","AMGN","ADI","AAPL","AMAT","ADSK","ADP","BIDU","BIIB","BMRN","AVGO","CA","CELG","CERN","CHTR","CHKP","CTAS","CSCO","CTXS","CTSH","CMCSA","COST","CSX","CTRP","XRAY","DISCA","DISCK","DISH","DLTR","EBAY","EA","EXPE","ESRX","FB","FAST","FISV","GILD","HAS","HSIC","HOLX","IDXX","ILMN","INCY","INTC","INTU","ISRG","JBHT","JD","KLAC","LRCX","LBTYA","LBTYK","LILA","LILAK","LVNTA","QVCA","MAT","MXIM","MCHP","MU","MSFT","MDLZ","MNST","MYL","NTES","NFLX","NCLH","NVDA","ORLY","PCAR","PAYX","PYPL","QCOM","REGN","ROST","SHPG","SIRI","SWKS","SBUX","SYMC","TMUS","TSLA","TXN","KHC","PCLN","TSCO","TRIP","FOXA","ULTA","VRSK","VRTX","VIAB","VOD","WBA","WDC","XLNX","YHOO")
  val stockData = STOCKS.map(X => Utils.readStock(X))
  val trainingData = stockData.map(_.take(10))
  val testData = stockData.map(_.drop(10))
  println(testData.map(_.length).sum / testData.length.toDouble)


  val qs = ListBuffer[Double]()
  val rs = ListBuffer[Double]()


  val kpls = ListBuffer[Double]()
  val spls = ListBuffer[Double]()
  val wpls = ListBuffer[Double]()

  for ((stock, i) <- stockData.zipWithIndex) {
    val testData = stock.drop(50)

    val KALPred = new KalmanPredictor(0.08, 0.96, stock.take(50), false)
    val KALPL = KALPred.evaluate(stock.take(50), stock.drop(50), 0d)._1

    val period = 15

    val SMAPred = new SMAPredictor(period)
    val SMAPL = SMAPred.evaluate(stock.drop(50-period).take(period), stock.drop(50), 0d)._1

    val WMAPred = new WMAPredictor(period)
    val WMAPL = WMAPred.evaluate(stock.drop(50-period).take(period), stock.drop(50), 0d)._1

    println(STOCKS(i))

    spls.append(SMAPL.last)
    kpls.append(KALPL.last)
    wpls.append(WMAPL.last)

    /*
    val fig0 = Figure()
    val plot0_0 = fig0.subplot(0)
    val plot0_1 = fig0.subplot(2,1,1)
    val plot0_2 = fig0.subplot(3,1,2)

    plot0_0 += plot(new DenseVector[Double](KALPred.smoothedValues.indices.map(_.toDouble).toArray), new DenseVector[Double](KALPred.smoothedValues.map(_(0)).toArray), '-')
    plot0_0 += plot(new DenseVector[Double](stock.data.indices.map(_.toDouble).toArray), new DenseVector[Double](stock.openings.toArray))

    plot0_1 += plot(new DenseVector[Double](SMAPred.smoothedValues.indices.map(_.toDouble).toArray), new DenseVector[Double](SMAPred.smoothedValues.map(_(0)).toArray), '-')
    plot0_1 += plot(new DenseVector[Double](stock.drop(50-period).data.indices.map(_.toDouble).toArray), new DenseVector[Double](stock.drop(50-period).openings.toArray))

    plot0_2 += plot(new DenseVector[Double](WMAPred.smoothedValues.indices.map(_.toDouble).toArray), new DenseVector[Double](WMAPred.smoothedValues.map(_(0)).toArray), '-')
    plot0_2 += plot(new DenseVector[Double](stock.drop(50-period).data.indices.map(_.toDouble).toArray), new DenseVector[Double](stock.drop(50-period).openings.toArray))
    */

    //if (pl.last > 200) println(s"High profits on ${STOCKS(i)} (${pl.last})")

  }

  println(s"Test over ${STOCKS.length} stocks")

  println(s"Kalman Filter:")
  println(s"Average profit: ${kpls.map(_ - 100).sum / kpls.length}")
  println(s"Stocks in profit %: ${kpls.count(_ > 100) * 100d/ kpls.length}")

  println(s"SMA:")
  println(s"Average profit: ${spls.map(_ - 100).sum / spls.length}")
  println(s"Stocks in profit %: ${spls.count(_ > 100) * 100d/ spls.length}")

  println(s"WMA:")
  println(s"Average profit: ${wpls.map(_ - 100).sum / wpls.length}")
  println(s"Stocks in profit %: ${wpls.count(_ > 100) * 100d/ wpls.length}")

  println("Kalman")
  println(kpls.mkString(","))
  println("Simple")
  println(spls.mkString(","))
  println("Weighted")
  println(wpls.mkString(","))

  for (i <- kpls.indices) {
    println(s"${i} ${kpls(i)} ${spls(i)} ${wpls(i)}")
  }

  val fig1 = Figure()
  val plot1_0 = fig1.subplot(0)
  val plot1_1 = fig1.subplot(2,1,1)
  val plot1_2 = fig1.subplot(3,1,2)
  //val plot_0_hist = fig1.subplot(2,1,1)
  plot1_0 += plot(new DenseVector[Double](kpls.indices.map(_.toDouble).toArray), new DenseVector[Double](kpls.map(_ - 100).toArray), '-', tips = (i) => STOCKS(i))
  plot1_0 += plot(new DenseVector[Double](kpls.indices.map(_.toDouble).toArray), new DenseVector[Double](Array.fill(kpls.length)(0d)))

  plot1_1 += plot(new DenseVector[Double](spls.indices.map(_.toDouble).toArray), new DenseVector[Double](spls.map(_ - 100).toArray), '-', tips = (i) => STOCKS(i))
  plot1_1 += plot(new DenseVector[Double](spls.indices.map(_.toDouble).toArray), new DenseVector[Double](Array.fill(spls.length)(0d)))

  plot1_2 += plot(new DenseVector[Double](wpls.indices.map(_.toDouble).toArray), new DenseVector[Double](wpls.map(_ - 100).toArray), '-', tips = (i) => STOCKS(i))
  plot1_2 += plot(new DenseVector[Double](wpls.indices.map(_.toDouble).toArray), new DenseVector[Double](Array.fill(wpls.length)(0d)))
  //plot_0_hist += hist(new DenseVector[Double](pls.toArray.map(_ - 100d)), HistogramBins.fromRange(-150, 150, 150))
  //plot_1 += plot(new DenseVector[Double](qs.toArray), new DenseVector[Double](rs.toArray), '+', labels = (i: Int) => STOCKS(i))

  plot1_0.xlabel = "Stock"
  plot1_0.ylabel = "Profit %"

  //plot_0_hist.xlabel = "Profit %"
  //plot_0_hist.ylabel = "Frequency"


  //plot_0_hist.xlim(-150, 150)
  /*
  val stock = Random.shuffle(stockData).head
  val predictor = new KalmanPredictor(stock.take(100))
  val pl = predictor.test(stock.drop(100))

  val fig = Figure()
  val plot_0 = fig.subplot(0)
  plot_0 += plot(new DenseVector[Double](pl.indices.map(_.toDouble).toArray), new DenseVector[Double](pl.toArray))
*/

  /*
  for (stock <- stockData) {
    println(stockData.indexOf(stock))
    new StocksOptimizer(List(0.0, 0.1), 1 to 10, stock, 10)
    val optimizer = new StocksOptimizer((0 to 200).map(_/400d),(0 to 200).map(_/200d), stock, 10)
    val res = optimizer.optimize()
    qs.append(res._1)
    rs.append(res._2)
  }

  val fig = Figure()
  val plot_0 = fig.subplot(0)

  plot_0 += plot(new DenseVector[Double](qs.toArray), new DenseVector[Double](rs.toArray), '+', labels = (i: Int) => STOCKS(i))
  plot_0.xlabel = "q"
  plot_0.ylabel = "r"
  plot_0.xlim = (0, 0.5)
  */

  /*
  var bestq, bestr, bestPL = 0d
  var bestqs: ListBuffer[Double] = ListBuffer()
  var bestpls: ListBuffer[Double] = ListBuffer()

  for (q <- (1 to 250).map(_/50000d)) {

    println(q)

    var qBestr = 0d
    var qBestPL = 0d

    for (r <- (1 to 40).map(_/40d)) {

      val pnls = new ListBuffer[Double]()
      for (i <- STOCKS.indices) {
        val filter = new StocksFilter(q, r, trainingData(i))
        filter.train()
        pnls.append(filter.test(testData(i)).last - 100d)
      }

      if (pnls.sum / pnls.length > qBestPL) {
        qBestPL = pnls.sum / pnls.length
        qBestr = r
        //println(s"q -> $q, r -> $qBestr, PL: $qBestPL")
      }

    }

    if (qBestPL > bestPL) {
      bestq = q
      bestr = qBestr
      bestPL = qBestPL
      println(s"q -> $bestq, r -> $bestr, PL: $bestPL")
    }

    bestqs.append(q)
    bestpls.append(qBestPL)

  }

  val fig = Figure()
  val plot_0 = fig.subplot(0)

  plot_0 += plot(new DenseVector[Double](bestqs.toArray), new DenseVector[Double](bestpls.toArray))
*/


  /*for (q <- (1 to 200).map(_/100000d)) {
    println(s"q -> $q")

    var qBestr = 0d
    var qBestPL = 0d

    for (r <- (1 to 50).map(_/50d)) {
      val pnls = new ListBuffer[Double]()
      for (i <- STOCKS.indices) {
        val filter = new StocksFilter(q, r, trainingData(i))
        filter.train()
        pnls.append(filter.test(testData(i)).last - 100d)
      }

      if (pnls.sum / pnls.length > qBestPL) {
        qBestPL = pnls.sum / pnls.length
        qBestr = r
        //println(s"q -> $q, r -> $qBestr, PL: $qBestPL")
      }
    }

    if (qBestPL > bestPL) {
      bestq = q
      bestr = qBestr
      bestPL = qBestPL
      println(s"q -> $bestq, r -> $bestr, PL: $bestPL")
    }
  }
  val q = bestq
  val r = bestr



  //TODO: Clean up kalman filter to separate prediction from actual calculations of error/ecv

  /*val q = 0.00162
  val r = 0.7*/

  val fig = Figure()
  val plot_0 = fig.subplot(0)

  for (i <- STOCKS.indices.take(1)) {
    val filter = new StocksFilter(q, r, trainingData(i))
    filter.train()
    val pnl = filter.test(testData(i)).map(_ - 100)
    plot_0 += plot(new DenseVector[Double](filter.xs.indices.map(_.toDouble).toArray), new DenseVector[Double](filter.xs.toArray))
    plot_0 += plot(new DenseVector[Double](stockData(i).openings.indices.map(_.toDouble).toArray), new DenseVector[Double](stockData(i).openings.toArray))
  }
  //plot_0 += plot(new DenseVector[Double](pnl.indices.map(_.toDouble).toArray), new DenseVector[Double](pnl.toArray))
  */

  /*
  val pnls = new ListBuffer[Double]()
  for (i <- STOCKS.indices) {
    val filter = new StocksFilter(0.01, 18/20d, trainingData(i))
    filter.train()
    pnls.append(filter.test(testData(i)).last - 100d)
  }
  println(pnls.sum / pnls.length)
  val fig = Figure()
  val plot_0 = fig.subplot(0)
  plot_0 += plot(new DenseVector[Double](pnls.indices.map(_.toDouble).toArray), new DenseVector[Double](pnls.toArray))
  plot_0 += plot(new DenseVector[Double](pnls.indices.map(_.toDouble).toArray), new DenseVector[Double](Array.fill(pnls.length)(0d)))
  */
  /*
  //val filter = new StocksFilter(0.767, -1.945, trainingData(40))

  //plot_0 += plot(new DenseVector[Double]((0 to 200).map(_.toDouble).toArray), new DenseVector[Double](Array.fill(201)(00d)))


  for (i <- trainingData.indices.toList.take(1)) {
    val filter = new StocksFilter(0.01, 18/20d, trainingData(i))
    filter.train()
    val pnl = filter.test(testData(i)).map(_ - 100)
    //plot_0 += plot(new DenseVector[Double](filter.xs.indices.map(_.toDouble).toArray), new DenseVector[Double](filter.xs.toArray))
    plot_0 += plot(new DenseVector[Double](pnl.indices.map(_.toDouble).toArray), new DenseVector[Double](pnl.toArray))
    //plot_0 += plot(new DenseVector[Double](stockData(i).openings.indices.map(_.toDouble).toArray), new DenseVector[Double](stockData(i).openings.toArray))
  }
  */


  /*

  var bestQ, bestR, bestPl = 0d
  var bestPLCurve: List[Double] = null

  for (q <- (750 to 790).map(_/1000d)) {
    println(q)
    for (r <- (-2500 to -1500).map(_/1000d).filter(_ != 0)) {
      val pls = ListBuffer[Double]()
      for (i <- STOCKS.indices) {
        val filter = new StocksFilter(q,r, trainingData(i))
        filter.train()
        val plCurve = filter.test(testData(i))
        pls.append(plCurve.last)
      }

      val average = pls.sum / pls.length
      if (average > bestPl) {
        bestQ = q
        bestR = r
        bestPl = average
        bestPLCurve = pls.toList
        println(s"q = $bestQ, r=$bestR -> $bestPl")
        /*val fig = Figure()
        val plot_0 = fig.subplot(0)
        plot_0 += plot(new DenseVector[Double](bestPLCurve.indices.map(_.toDouble).toArray), new DenseVector[Double](bestPLCurve.toArray))*/
      }

    }
  }
  println(s"q = $bestQ, r=$bestR -> $bestPl")
  val fig = Figure()
  val plot_0 = fig.subplot(0)
  plot_0 += plot(new DenseVector[Double](bestPLCurve.indices.map(_.toDouble).toArray), new DenseVector[Double](bestPLCurve.toArray))
  plot_0 += plot(new DenseVector[Double](bestPLCurve.indices.map(_.toDouble).toArray), new DenseVector[Double](Array.fill(bestPLCurve.length)(100d)))
  */


}

  /*4

   for (i <- STOCKS.indices) {
    val filter = new StocksFilter(0.1,1, trainingData(i))
    filter.train()
    val plCurve = filter.test(testData(i))
    println(plCurve.last)
    pls.append(plCurve.last)

  }

  val fig = Figure()
  val plot_0 = fig.subplot(0)
  plot_0 += plot(new DenseVector[Double](pls.indices.map(_.toDouble).toArray), new DenseVector[Double](pls.toArray))

  def inv(d: Double) = Math.pow(d, -1)

  val k = new KalmanFilter(null, null, null, null) with FilterTracking

  def kalmanFilter(stockData: StockHistory)(R: Double, Q: Double) : (List[Double], List[Boolean]) = {

    val winLoss:ListBuffer[Boolean] = ListBuffer()

    val x = ListBuffer[Double](stockData.head._2)
    val p = ListBuffer[Double](1)

    for (k <- stockData.data.indices.drop(1)) {



      //Time Update
      x.append(xk)
      p.append(pk)

      if (k < stockData.openings.length-1) {
        if (xk > stockData.openings(k)) {
          //Suggests a buy
          if (stockData.openings(k + 1) > stockData.openings(k)) {
            winLoss.append(true)
          } else {
            winLoss.append(false)
          }

        } else if (xk < stockData.openings(k)) {
          if (stockData.openings(k + 1) < stockData.openings(k)) {
            winLoss.append(true)
          } else {
            winLoss.append(false)
          }
        }
      }
    }

    (x.toList, winLoss.toList)

  }

  /**
    * IDEAS:
    * Create a Kalman state which stores both the state and config parameters
    * "Train" a filter on X days of data and then try and test its predictions
    * Take those predictions and try and find P/L
    * Find best Q and R for these
    */

  val STOCKS = List("III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP")
  val stockData = STOCKS.map(X => new StockHistory(Utils.readStock(X)))
  val filters = stockData.map(S => kalmanFilter(S) _)

  var bestR, bestQ, bestW = 0d

  println(s"Testing the Kalman filter on ${STOCKS.length} stocks")


  //q -> 0.0, r -> -2.0, w -> 0.6190476190476191
  for (q <- Range(-50, 50).map(_/10d)) {
    if ((q / 0.1) % 1 == 0) println(q)

    for (r <- Range(-50, 50).map(_/10d)) {

      val winLoss = filters.map(_(r, q)).flatMap(_._2)
      println(winLoss.length)
      val wins = winLoss.count(if(_) true else false).toDouble
      if (wins/winLoss.length > bestW) {
        bestR = r
        bestQ = q
        bestW = wins/winLoss.length
        println(s"q -> $q, r -> $r, w -> $bestW")
      }
      //println(s"($q, $r) -> ${wins / winLoss.length}")

    }

  }

  println(s"q -> $bestQ, r -> $bestR, w -> $bestW")


  /*
  val wins = winLoss.count(if(_) true else false).toDouble
  println(wins / winLoss.length)
  println(winLoss)

  val fig = Figure()
  val plot_0 = fig.subplot(0)
  val plot_1 = fig.subplot(2,1,1)
  plot_0 += plot(new DenseVector[Double](stockData.openings.indices.map(_.toDouble).toArray), new DenseVector[Double](x.toArray))
  plot_0 += plot(new DenseVector[Double](stockData.openings.indices.map(_.toDouble).toArray), new DenseVector[Double](stockData.openings.toArray))
  plot_1 += plot(new DenseVector[Double](stockData.openings.indices.map(_.toDouble).take(winLoss.length).toArray), new DenseVector[Double](winLoss.map(if (_) 1d else 0d).toArray))*/



}
*/