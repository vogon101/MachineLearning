package com.vogonjeltz.machineInt.evoTanks.networks

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.linalg._
import breeze.numerics._

import scala.util.Random

/**
  * Created by Freddie on 29/03/2017.
  */
class NeuralNetwork(val connections: DenseMatrix[Double], val inputWeights: DenseVector[Double], val outputWeights: DenseVector[Double], val activationFunction: (Double) => Double, val geneticAge: Int = 1) {

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
      val n = last + (Random.nextGaussian() / delta) - 0.5/delta
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
        geneticAge + 1
      )
      ).toList
  }

}

object NeuralNetwork {

  def cross(netA: NeuralNetwork, netB: NeuralNetwork): NeuralNetwork = {

    def splitAndCombineVector(lA: DenseVector[Double], lB: DenseVector[Double]): DenseVector[Double] = {
      assert(lA.length == lB.length)
      val splitPoint = Random.nextInt(lA.length)
      DenseVector.vertcat(lA.slice(0,splitPoint), lB.slice(splitPoint, lA.length))
    }

    def splitAndCombineMatrix(mA: DenseMatrix[Double], mB: DenseMatrix[Double]): DenseMatrix[Double] = {
      assert(mA.rows == mB.rows)
      new DenseMatrix[Double](mA.rows, mA.rows, splitAndCombineVector(DenseVector(mA.data), DenseVector(mB.data)).toArray)
    }

    new NeuralNetwork(
      splitAndCombineMatrix(netA.connections, netB.connections),
      splitAndCombineVector(netA.inputWeights, netB.inputWeights),
      splitAndCombineVector(netA.outputWeights, netB.outputWeights),
      List(netA.activationFunction, netB.activationFunction)(Random.nextInt(2)),
      (netA.geneticAge + netB.geneticAge)/2 + 1
    ).spawn(1).head

  }

}
