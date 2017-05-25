package com.vogonjeltz.trading.app

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.plot._
import com.vogonjeltz.trading.lib.networks.{NetworkStorage, NeuralNetwork}
import com.vogonjeltz.trading.lib.networks.evolution.{EvolutionCoordinator, EvolutionSettings}

import scala.collection.mutable.ListBuffer
import scala.util.Random
import net.liftweb.json.compactRender

/**
  * MNISTTest
  *
  * Created by fredd
  */
object MNISTTest extends App {

  def testNetwork(testSamples: List[(Int, DenseVector[Double])], network: NeuralNetwork): Double = {
    var wins = 0

    for (d <- testSamples) {

      network.reset()
      for (i <- 0 until ex.settings.tickTime) {
        network.tick(DenseVector((d._2.toArray.toList ::: List.fill(network.size - d._2.length)(0d)).toArray))
      }

      //TODO: Investigate this: FIXME FIXME
      val output = network.output.toArray.drop(network.size - 10).zipWithIndex.sortWith((X,Y) => X._1 > Y._1)
      val predicted = 9 - output.head._2
      val actual = d._1

      if (predicted == actual) {
        wins += 1
      }

    }

    val score = (wins / testSamples.length.toDouble) * 100

    println("Results of testing (%correct):")
    println(score)

    score
  }



  val data = Random.shuffle(Utils.readMinst())
  val settings = EvolutionSettings(
    generationSize = 200,
    evolutionDelta = 10,
    populationSize = 60,
    tickTime = 4,
    harshness = 7,
    geneticDiversityQuota = 15
  )

  def accessData(generation: Int, round: Int) = {
    data((generation * settings.generationSize + round) % data.length)
  }

  println("Welcome to memory hell")
  print("Enter a command >")

  val bestFitness: ListBuffer[Double] = ListBuffer()
  val averageFitness: ListBuffer[Double] = ListBuffer()
  val testScores: ListBuffer[Double] = ListBuffer()
  val fig1 = Figure()
  //val worstFitness: ListBuffer[Double] = ListBuffer()

  val networkSize = (28 * 28) + 50 + 10

  val ex: EvolutionCoordinator = new EvolutionCoordinator(
    (gen, round, i, o) => {
      val expectedLabel = accessData(gen, round)._1
      val nums: List[Double] = List(0,1,2,3,4,5,6,7,9).filterNot(_ == expectedLabel).map(networkSize - 1 - _).map(o(_))

      val output = o.toArray.drop(networkSize - 10).zipWithIndex.sortWith((X,Y) => X._1 > Y._1)
      val predicted = 9 - output.head._2

      o(networkSize - 1 - expectedLabel) * 4 - nums.sum + ((if (predicted == expectedLabel) 10 else -10) * output.map(_._1).max)
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
      val rounds = (0 to ex.settings.generationSize).map(
        r => DenseVector(
          (accessData(g,r)._2.toArray.toList ::: List.fill(networkSize - data.head._2.length)(0d)).toArray
        )
      )
      r => rounds(r)
    },
    generationHook = g => {

      println(s"${g.genNumber} - Average: ${g.averageFitness} Best: ${g.fitnesses.max}")
      bestFitness.append(g.fitnesses.max)
      //worstFitness.append(g.fitnesses.min)
      averageFitness.append(g.averageFitness)

      val plot_0 = fig1.subplot(0)
      plot_0 += plot(DenseVector(bestFitness.indices.map(_.toDouble).toArray), DenseVector(bestFitness.toArray))
      plot_0.title = s"Best Fitnesses over ${g.genNumber} Generations"

      //val plot_1 = fig1.subplot(2,1,1)
      //plot_1 += plot(DenseVector(worstFitness.indices.map(_.toDouble).toArray), DenseVector(worstFitness.toArray))

      val plot_2 = fig1.subplot(2,1,1)
      plot_2 += plot(DenseVector(averageFitness.indices.map(_.toDouble).toArray), DenseVector(averageFitness.toArray))
      plot_2.title = s"Average Fitness over ${g.genNumber} Generations"

      val plot_3 = fig1.subplot(3,1,2)
      plot_3 += plot(DenseVector(testScores.indices.map(_.toDouble).toArray), DenseVector(testScores.toArray))
      plot_3.title = s"Test Scores over ${g.genNumber} Generations"
      plot_3.ylim(0, 100)


      if (g.genNumber % 5 == 0) {

        if (g.genNumber > 2) {
          println(s"Taking snapshot (Generation ${g.genNumber})")
          Utils.writeText(
            compactRender(NetworkStorage.evolutionCoordinatorSnapshot(ex)),
            "output/network.json"
          )
          println("Snapshot saved")
        }

        println("Testing Network")
        if (g.genNumber % 30 == 0 && g.genNumber > 2) {
          println("Large scale test")
          testScores.append(testNetwork(data.take(5000), ex.best.get._1))

        } else {
          testScores.append(testNetwork(data.take(2500), ex.best.get._1))
        }
      }

    }, settings = settings
  )

  val command = io.StdIn.readLine()

  if (command == "run") {
    ex.init()
    while (true) {
      ex.runGeneration(ex.lastGeneration + 1)
    }
  } else if (command == "test") {
    //TODO: Test with 1000s of data points

    NetworkStorage.readToEvolutionCoordinator(Utils.readText("output/network.json"), ex)
    testNetwork(data.take(30000), ex.best.get._1)

  } else if (command == "continue") {
    NetworkStorage.readToEvolutionCoordinator(Utils.readText("output/network.json"), ex)
    println("Testing loaded network")
    testNetwork(data.take(10000), ex.lastBestNetwork)
    while (true) {
      ex.runGeneration(ex.lastGeneration + 1)
    }
  }

  /*
  val fig1 = Figure()
  val plot_0 = fig1.subplot(0)
  plot_0 += plot(DenseVector(bestFitness.indices.map(_.toDouble).toArray), DenseVector(bestFitness.toArray))

  //val plot_1 = fig1.subplot(2,1,1)
  //plot_1 += plot(DenseVector(worstFitness.indices.map(_.toDouble).toArray), DenseVector(worstFitness.toArray))

  val plot_2 = fig1.subplot(2,1,1)
  plot_2 += plot(DenseVector(averageFitness.indices.map(_.toDouble).toArray), DenseVector(averageFitness.toArray))
  */

}

