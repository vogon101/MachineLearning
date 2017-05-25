package com.vogonjeltz.trading.lib.networks.evolution

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.networks.NeuralNetwork


/**
  * Created by Freddie on 29/03/2017.
  */
class Generation(val genNumber: Int, val population: List[(NeuralNetwork, DenseVector[Double])], val fitnesses: DenseVector[Double], val averageFitness: Double) {


}
