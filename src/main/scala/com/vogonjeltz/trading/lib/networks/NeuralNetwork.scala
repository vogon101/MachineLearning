package com.vogonjeltz.trading.lib.networks

import breeze.linalg.{DenseMatrix, DenseVector}

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by Freddie on 29/03/2017.
  */
class NeuralNetwork(val connections: DenseMatrix[Double], val inputWeights: DenseVector[Double], val outputWeights: DenseVector[Double], activationFunction: (Double) => Double, val geneticage: Int = 1) {

  assert(connections.cols == connections.rows)
  assert(inputWeights.length == connections.rows)

  var state: DenseVector[Double] = DenseVector.zeros(connections.cols)

  lazy val size = inputWeights.length

  def tick(in: DenseVector[Double]): Unit = {

    assert(in.length == connections.rows)

    //Inputs
    state = state + (inputWeights * in)

    //Progress the state
    state = connections * state
    state = state.map(activationFunction)

  }

  def output: DenseVector[Double] = state * outputWeights


  def reset():Unit = state = DenseVector.zeros(connections.cols)

  def spawn(n: Int, delta: Double = 50): List[NeuralNetwork] = {

    def nextNum(last: Double): Double = {
      val n = last + (Random.nextDouble() / delta) - 0.5/delta
      if (n < -1) -1
      else if (n > 1) 1
      else n
    }

    (for (i <- 0 until n)
      yield new NeuralNetwork(
        connections.map(nextNum),
        inputWeights.map(nextNum),
        outputWeights.map(nextNum),
        activationFunction,
        geneticage + 1
      )
      ).toList
  }


}
