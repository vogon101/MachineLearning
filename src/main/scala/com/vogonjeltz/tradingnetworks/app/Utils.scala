package com.vogonjeltz.tradingnetworks.app

import com.vogonjeltz.tradingnetworks.lib.StockHistory

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 03/04/2017.
  */
object Utils {

  def readStock(code: String): StockHistory = {
    //println(s"Reading stock $code")
    val bufferedSource = io.Source.fromFile(s"data/$code.csv")
    //(Date, Open, Close)
    val stockHistory: ListBuffer[(String, Double, Double)] = new ListBuffer[(String, Double, Double)]()
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      stockHistory.append((
        cols(0), cols(1).toDouble, cols(4).toDouble
      ))
    }
    bufferedSource.close
    new StockHistory(code, stockHistory.toList)
  }

}
