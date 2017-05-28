package com.vogonjeltz.trading.lib.models.kalman

import breeze.linalg._
import com.vogonjeltz.trading.lib.models.CleverPredictor
import com.vogonjeltz.trading.lib.{AutoCorrelation, StockHistory}
import com.vogonjeltz.trading.lib.prediction.StocksPredictor

import scala.collection.mutable.ListBuffer

/**
  * KalmanPredictor
  *
  * Created by fredd
  */
class KalmanPredictor(q: Double, r: Double, val trainingData: StockHistory, val useCleverPrediction: Boolean = true)
  extends KalmanFilter( new KalmanState(q, r, DenseVector[Double](trainingData.openings.head), 0) ) with CleverPredictor {

  assert(trainingData.length >= 10, "Not enough data to train filter")

  override def model_predict() = predictKalman().x

  override def model_update(d: DenseVector[Double]) = update(d).x
}
  /*
  var daysPast: StockHistory = trainingData
  val residuals: ListBuffer[Double] = ListBuffer()


  val predictedSmoothedQuadratic: ListBuffer[Double] = ListBuffer()
  val predictedResiduals: ListBuffer[Double] = ListBuffer()
  val predictedValues: ListBuffer[Double] = ListBuffer()

  private def trackPredictions(predictedSmoothed: Double, predictedResidual: Double, predictedValue: Double) = {
    predictedSmoothedQuadratic.append(predictedSmoothed)
    predictedResiduals.append(predictedResidual)
    predictedValues.append(predictedValue)
  }

  private def trackReals(realDay: (String, Double, Double), realResidual: Double) = {
    daysPast = daysPast.+(realDay._1, realDay._2, realDay._3)
    residuals.append(realResidual)
  }

  def predictDay(): Double = {

    val smoothed = predictKalman()

    val predictedSoothed = quadraticPrediction(
      smoothedValues(smoothedValues.length - 3)(0),
      smoothedValues(smoothedValues.length - 2)(0),
      smoothedValues.last(0)
    )

    val predictedResidual = predictNextResidual(residuals.toList)
    trackPredictions(predictedSoothed, predictedResidual, predictedSoothed + predictedResidual)

    predictedSoothed// + (if (useCleverPrediction) predictedResidual else 0)

  }

  def correctForDay(prediction: Double, day: (String, Double, Double)): Double = {

    val residual = day._2 - smoothedValues.last(0)

    trackReals(day, residual)

    update(DenseVector[Double](day._2))
    residual

  }


  override def trainingStage(): Unit = {
    for (day <- trainingData) {
      val p = predictKalman()
      update(DenseVector[Double](day._2))
      trackPredictions(p.x(0), 0, p.x(0))
      trackReals(day, day._2 - p.x(0))
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

    val coeffs = AutoCorrelation.getCoeffs(rs.toArray)
    AutoCorrelation.predict(coeffs, rs.toArray, 1)

  }

}
*/
