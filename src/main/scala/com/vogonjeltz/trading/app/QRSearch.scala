package com.vogonjeltz.trading.app

import com.vogonjeltz.trading.lib.models.kalman.KalmanPredictor

/**
  * Created by Freddie on 28/05/2017.
  */
object QRSearch extends App{

  var bestQ = 0d
  var bestR = 0d
  var bestProfit = 0d

  val STOCKS = List("III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP","ATVI","ADBE","AKAM","ALXN","GOOG","GOOGL","AMZN","AAL","AMGN","ADI","AAPL","AMAT","ADSK","ADP","BIDU","BIIB","BMRN","AVGO","CA","CELG","CERN","CHTR","CHKP","CTAS","CSCO","CTXS","CTSH","CMCSA","COST","CSX","CTRP","XRAY","DISCA","DISCK","DISH","DLTR","EBAY","EA","EXPE","ESRX","FB","FAST","FISV","GILD","HAS","HSIC","HOLX","IDXX","ILMN","INCY","INTC","INTU","ISRG","JBHT","JD","KLAC","LRCX","LBTYA","LBTYK","LILA","LILAK","LVNTA","QVCA","MAT","MXIM","MCHP","MU","MSFT","MDLZ","MNST","MYL","NTES","NFLX","NCLH","NVDA","ORLY","PCAR","PAYX","PYPL","QCOM","REGN","ROST","SHPG","SIRI","SWKS","SBUX","SYMC","TMUS","TSLA","TXN","KHC","PCLN","TSCO","TRIP","FOXA","ULTA","VRSK","VRTX","VIAB","VOD","WBA","WDC","XLNX","YHOO")
  val stockData = STOCKS.map(X => Utils.readStock(X))
  val trainingData = stockData.map(_.take(10))
  val testData = stockData.map(_.drop(10))

  for (q <- Range(1,10).map(_/100d)) {
    println(s"Q: $q")
    for (r <- Range(90, 99).map(_/100d)) {
      println(s"R: $r")
      var totalProfit = 0d
      for ((stock, i) <- stockData.zipWithIndex) {
        val KALPred = new KalmanPredictor(q, r, stock.take(50), false)
        val KALPL = KALPred.evaluate(stock.take(50), stock.drop(50), 0.005d)._1

        totalProfit += KALPL.last

      }

      if (totalProfit > bestProfit) {
        bestQ = q
        bestR = r
        bestProfit = totalProfit
      }
    }
  }

  println(s"q -> $bestQ")
  println(s"r -> $bestR")
  println(s"profit: $bestProfit")


}
