package com.vogonjeltz.machineInt.app

import com.vogonjeltz.machineInt.lib.StockHistory
import com.vogonjeltz.machineInt.lib.networks.evolution.{EvolutionCoordinator, EvolutionSettings}

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Stocks
  *
  * Created by fredd
  */
object Stocks extends App {

  /*
  def readStock(code: String): List[(String, Double, Double)] = {
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
    stockHistory.toList
  }

  val STOCKS = List("III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP")
  val stockData = ListBuffer[StockHistory]()

  var lastDay = 240

  def get(gen: Int) : StockHistory = {
    val r = new Random(gen)
    val i = r.nextInt(STOCKS.length)
    stockData(i)
  }

  for (stock <- STOCKS) {
    stockData.append(new StockHistory(stock, readStock(stock)))
    println(
      if (stockData.last.data(lastDay)._3 > stockData.last.data(lastDay)._2) s"BUY $stock" else s"SELL $stock"
    )
  }
  println(stockData.head.data.length)

  val ec = new EvolutionCoordinator(
    (gen, in, out) => {
      //(get(gen).data(lastDay)._3 - get(gen).data(lastDay)._2) * out.data.last
      //Buy
      if (out.data.last > 1) {
        if (stockData(gen % stockData.length).data(lastDay)._3 > stockData(gen % stockData.length).data(lastDay)._2) Math.pow(out.data.last, 4)
        else -out.data.last
      } //Short
      else if (out.data.last < -1) {
        if (stockData(gen % stockData.length).data(lastDay)._3 < stockData(gen % stockData.length).data(lastDay)._2) Math.pow(out.data.last, 4)
        else out.data.last
      } // Do nothing
      else {
        Math.abs(out.data.last) - 0.3
      }
    },
    () => {
      val matrix = Matrix.ofDim(20, 20)((x: Int, y: Int) => (Random.nextDouble() * 2) - 1)

      new NeuralNetwork(matrix, (a: Double) => {
        5 * Math.tanh(0.6 * a)
      }
      )
    },
    gen => {
      tick => Vect(get(gen).data(tick)._2, get(gen).data(tick)._3).fill(20  , 0)
    },
    new EvolutionSettings(tickTime = lastDay, populationSize = 30),
    generation => {
      var pl = 30d * 100
      var winCount = 0
      for ((network,state) <- generation.population) {
        val open  = get(generation.genNumber).data(lastDay)._2
        val close = get(generation.genNumber).data(lastDay)._3
        if (state.data.last > 1) {
          pl += (close - open) / 100
          if (close > open) winCount += 1
        } else if (state.data.last < -1) {
          pl -= (close - open) / 100
          if (open > close) winCount += 1
        }
      }
      println(generation.genNumber + ", " + (pl / (30 * 100)) + ", " + generation.averageFitness + ", " + winCount)
    }
  )
  ec.run(20000)
  */

}
