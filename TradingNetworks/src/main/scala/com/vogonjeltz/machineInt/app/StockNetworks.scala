package com.vogonjeltz.machineInt.app

import breeze.linalg.{DenseMatrix, DenseVector}
import com.vogonjeltz.machineInt.app.MNISTTest.networkSize
import com.vogonjeltz.machineInt.lib.networks.NeuralNetwork
import com.vogonjeltz.machineInt.lib.networks.evolution.{EvolutionCoordinator, EvolutionSettings}

import scala.util.Random

/**
  * Created by Freddie on 17/05/2017.
  */
object StockNetworks {

  val settings = EvolutionSettings()

  val networkSize = 100

  val ec = new EvolutionCoordinator(
    fitnessFunction = ???,
    networkCreator = () => {
      val matrix = DenseMatrix.tabulate(networkSize, networkSize)((x: Int, y: Int) => Random.nextDouble() * 2 - 1d)
      val inputWeights = DenseVector.tabulate(networkSize)(i => Random.nextDouble() * 2 - 1d)
      val outputWeights = DenseVector.tabulate(networkSize)(i => Random.nextDouble() * 2 - 1d)

      new NeuralNetwork(matrix, inputWeights, outputWeights, (a: Double) =>
        Math.tanh(a)
      )
    },
    inputFunction = ???,
    settings = settings,
    generationHook = ???
  )


}
