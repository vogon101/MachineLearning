package com.vogonjeltz.trading.lib.models

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.prediction.StocksPredictor
import com.vogonjeltz.trading.lib.{AutoCorrelation, StockHistory}

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 26/05/2017.
  */
trait CleverPredictor extends StocksPredictor{

  var daysPast: StockHistory = new StockHistory("", List())
  val residuals: ListBuffer[Double] = ListBuffer()


  val predictedSmoothedQuadratic: ListBuffer[Double] = ListBuffer()
  val predictedResiduals: ListBuffer[Double] = ListBuffer()
  val predictedValues: ListBuffer[Double] = ListBuffer()
  val smoothedValues:ListBuffer[DenseVector[Double]] = ListBuffer()

  def model_predict(): DenseVector[Double]
  def model_update(d : DenseVector[Double]): DenseVector[Double]


  private def trackPredictions(predictedSmoothed: DenseVector[Double], predictedSmoothedQuadraticPoint: Double, predictedResidual: Double, predictedValue: Double) = {
    smoothedValues.append(predictedSmoothed)
    predictedSmoothedQuadratic.append(predictedSmoothedQuadraticPoint)
    predictedResiduals.append(predictedResidual)
    predictedValues.append(predictedValue)
  }

  private def trackReals(realDay: (String, Double, Double), realResidual: Double) = {
    daysPast = daysPast.+(realDay._1, realDay._2, realDay._3)
    residuals.append(realResidual)
  }

  def predictDay(): Double = {

    val smoothed = model_predict()

    val predictedSoothed = quadraticPrediction(
      smoothedValues(smoothedValues.length - 3)(0),
      smoothedValues(smoothedValues.length - 2)(0),
      smoothedValues.last(0)
    )

    val predictedResidual = predictNextResidual(residuals.toList)
    trackPredictions(smoothed, predictedSoothed, predictedResidual, predictedSoothed + predictedResidual)

    predictedSoothed// + (if (useCleverPrediction) predictedResidual else 0)

  }

  def correctForDay(prediction: Double, day: (String, Double, Double)): Double = {

    val residual = day._2 - smoothedValues.last(0)

    trackReals(day, residual)

    model_update(DenseVector[Double](day._2))
    residual

  }


  override def trainingStage(trainingData: StockHistory): Unit = {
    daysPast = trainingData
    for (day <- trainingData) {
      val p = model_predict()
      model_update(DenseVector[Double](day._2))
      trackPredictions(p, p(0), 0, p(0))
      trackReals(day, day._2 - p(0))
    }
  }

  def quadraticPrediction(d0: Double, d1: Double, d2: Double): Double = {

    val g1 = d1 - d0
    val g2 = d2 - d1
    val delta = g2 - g1
    val g3 = g2 + delta

    d2 + g3

  }


  def predictNextResidual(rs: List[Double]): Double = {

    //val coeffs = AutoCorrelation.getCoeffs(rs.toArray)
    //AutoCorrelation.predict(coeffs, rs.toArray, 1)
    rs.last

  }

}

