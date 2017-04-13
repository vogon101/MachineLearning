package com.vogonjeltz.tradingnetworks.lib.networks

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by Freddie on 29/03/2017.
  */
class NeuralNetwork(val connections: Matrix, activationFunction: (Double) => Double) {

  assert(connections.square)

  var state: Vect = Vect.ZERO(connections.cols)

  def tick(in: Vect): Unit = {

    assert(in.length == connections.rows)
    val newState: ListBuffer[Double] = new ListBuffer[Double]()
    for (i <- 0 until in.length) {
      val inputs = connections.row(i)
      var accumulator: Double = in(i)
      for ((w, j) <- inputs.zipWithIndex) {
        accumulator += w * state(j)
      }
      newState.append(activationFunction(accumulator))
    }

    state = Vect(newState.toList)

  }

  def reset() = state = Vect.ZERO(connections.cols)

  def spawn(n: Int): List[NeuralNetwork] = (for (i <- 0 until n)
    yield new NeuralNetwork(connections.transform(_ + (Random.nextDouble()/50) - 0.01), activationFunction)).toList

}
