package com.vogonjeltz.machineInt.evoTanks.networks.evolution

import breeze.linalg.DenseVector
import com.vogonjeltz.machineInt.evoTanks.networks.NeuralNetwork

import scala.collection.mutable.ListBuffer

/**
  * Created by Freddie on 29/03/2017.
  */
class EvolutionCoordinator(
                          val fitnessFunction: (Int, Int, Int => DenseVector[Double], DenseVector[Double]) => Double,
                          val networkCreator: () => NeuralNetwork,
                          val inputFunction: (Int)=>(Int) => DenseVector[Double],
                          val settings: EvolutionSettings = EvolutionSettings(),
                          val generationHook: (Generation) => Unit = (g) => (),
                          val defaultActivationFunction: Double => Double = Math.tanh _
                                              ) {

  var population: List[NeuralNetwork] = List()
  var best: Option[(NeuralNetwork, Double)] = None
  var lastGeneration: Int = 0
  var lastAverageFitness: Double = 0
  var lastBestNetwork: NeuralNetwork = _

  def init(): Unit = {
    population = (for (i <- 0 until settings.populationSize) yield networkCreator()).toList
  }

  def run(n: Int) = {
    for (i <- lastGeneration until (lastGeneration + n)) runGeneration(i)
  }

  def runGeneration(genNumber: Int): Generation = {

    val input = inputFunction(genNumber)
    val fitnesses:List[Double] = population.map(network => {
        (0 to settings.generationSize).toList.map(round => {
          network.reset()
          val thisInput = input(round)
          for (i <- 0 until settings.tickTime) {
            network.tick(thisInput)
          }
          fitnessFunction(genNumber, round, input, network.output)
        })
      }).map(X => X.sum / X.length)


    val newPopulation = evolve(population, fitnesses)
    val result = new Generation(genNumber, population.zip(population.map(_.output)), DenseVector(fitnesses.toArray), fitnesses.sum / fitnesses.length)
    lastGeneration = genNumber
    lastAverageFitness = result.averageFitness
    //println(genNumber + "," + result.averageFitness)
    //println(result.population.map(_.connections.data.map(_.sum).sum).sum / (population.head.connections.cols * population.head.connections.cols * population.length))
    //println(result.averageFitness)
    population = newPopulation
    generationHook(result)
    result
  }

  def evolve(networks: List[NeuralNetwork], fitnesses: List[Double]): List[NeuralNetwork] = {
    val orderedNetworks = networks.zip(fitnesses).sortWith((A, B) => A._2 > B._2)
    lastBestNetwork = orderedNetworks.head._1
    val avgF = fitnesses.sum / fitnesses.length
    if (best.isDefined) {
      //TODO: Switch to actual fitness, not average
      if (orderedNetworks.head._2 > best.get._2) {
        best = Some(orderedNetworks.head)
        println("--- Found new high fitness network")
        println(s"New best fitness: ${best.get._2}")
        println(s"Network age: ${best.get._1.geneticAge}")
      }
    } else  best = Some((orderedNetworks.head._1, avgF))

    var newNetworks: ListBuffer[NeuralNetwork] = new ListBuffer()
    for (i <- 0 to settings.geneticDiversityQuota) newNetworks.append(networkCreator())
    var i = 0
    while (newNetworks.length <= orderedNetworks.length) {
      newNetworks.appendAll(orderedNetworks(i)._1.spawn(Math.floor((orderedNetworks.length - i) / settings.harshness).toInt, settings.evolutionDelta))
      i += 1
    }
    newNetworks.toList.take(orderedNetworks.length)
  }


}
