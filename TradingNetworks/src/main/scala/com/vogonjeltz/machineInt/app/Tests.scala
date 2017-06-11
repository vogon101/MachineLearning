package com.vogonjeltz.machineInt.app

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.plot._
import com.vogonjeltz.machineInt.lib.networks.NeuralNetwork
import com.vogonjeltz.machineInt.lib.networks.evolution.EvolutionCoordinator

import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * Created by Freddie on 29/03/2017.
  */
object Tests  extends App{

  val networkSize = 6
  val bestFitness: ListBuffer[Double] = ListBuffer()
  val averageFitness: ListBuffer[Double] = ListBuffer()
  val worstFitness: ListBuffer[Double] = ListBuffer()


  val ec = new EvolutionCoordinator(
    (gen, r, i,o) => {
      1 / (i(0) + o.map(-_)).map(Math.abs _).sum
      //Math.abs(1/i.diff(o))
    },
    () => {
      val matrix = DenseMatrix.tabulate(networkSize, networkSize)((x: Int, y: Int) => Random.nextDouble() * 2 - 1d)
      val inputWeights = DenseVector.tabulate(networkSize)(i => Random.nextDouble() * 2 - 1d)
      val outputWeights = DenseVector.tabulate(networkSize)(i => Random.nextDouble() * 2 - 1d)

      new NeuralNetwork(matrix, inputWeights, outputWeights, (a: Double) =>
        Math.tanh(a)
      )
    },
    g => {
          val x = DenseVector(Array.fill(networkSize)(Random.nextDouble()))
          c => x
    },
    generationHook = g => {

      bestFitness.append(g.fitnesses.max)
      worstFitness.append(g.fitnesses.min)
      averageFitness.append(g.averageFitness)

    }
  )

  ec.run(1000)

  val fig1 = Figure()
  val plot_0 = fig1.subplot(0)
  plot_0 += plot(DenseVector(bestFitness.indices.map(_.toDouble).toArray), DenseVector(bestFitness.toArray))

  val plot_1 = fig1.subplot(2,1,1)
  plot_1 += plot(DenseVector(worstFitness.indices.map(_.toDouble).toArray), DenseVector(worstFitness.toArray))

  val plot_2 = fig1.subplot(3,1,2)
  plot_2 += plot(DenseVector(averageFitness.indices.map(_.toDouble).toArray), DenseVector(averageFitness.toArray))

  /*
  val matrix = Matrix.ofDim(10,10)((x: Int, y: Int) => {
    (Random.nextDouble() * 2) - 1
  })
  println(matrix)

  val nn = new NeuralNetwork(matrix, (a: Double) =>
    if (a < -1) -1
    else if (a > 1) 1
    else a
  )
  nn.tick(Vect.ofDim(10)((i: Int) => Random.nextDouble()))
  println(nn.state)
  for (i <- 0 to 10) {
    nn.tick(Vect.ZERO(10))
    println(nn.state)
  }*/

}
