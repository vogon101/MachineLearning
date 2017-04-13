package com.vogonjeltz.tradingnetworks.lib.networks.evolution

import com.vogonjeltz.tradingnetworks.lib.networks.Vect
import com.vogonjeltz.tradingnetworks.lib.networks.{NeuralNetwork, Vect}

/**
  * Created by Freddie on 29/03/2017.
  */
class EvolutionCoordinator(
                                                val fitnessFunction: (Int, Int => Vect, Vect) => Double,
                                                val networkCreator: () => NeuralNetwork,
                                                val inputFunction: (Int)=>(Int) => Vect,
                                                val settings: EvolutionSettings = EvolutionSettings(),
                                                val generationHook: (Generation) => Unit = (g) => ()
                                              ) {

  var population: List[NeuralNetwork] = (for (i <- 0 until settings.populationSize) yield networkCreator()).toList

  def run(n: Int) = {
    for (i <- 0 until n) runGeneration(i)
  }

  def runGeneration(genNumber: Int): Generation = {

    population.foreach(_.reset())
    val input = inputFunction(genNumber)
    val fitnesses: List[Double] = population.map(network => {
      for (i <- 0 until settings.tickTime) {
        network.tick(input(i))
      }
      fitnessFunction(genNumber, input, network.state)
    })

    val newPopulation = evolve(population, fitnesses)
    val result = new Generation(genNumber, population.zip(population.map(_.state)), fitnesses.sum / fitnesses.length)
    //println(genNumber + "," + result.averageFitness)
    //println(result.population.map(_.connections.data.map(_.sum).sum).sum / (population.head.connections.cols * population.head.connections.cols * population.length))
    population = newPopulation
    generationHook(result)
    result
  }

  def evolve(networks: List[NeuralNetwork], fitnesses: List[Double]): List[NeuralNetwork] = {
    val orderedNetworks = networks.zip(fitnesses).sortWith((A, B) => A._2 > B._2).map(_._1)
    orderedNetworks.zipWithIndex.flatMap(
      n => n._1.spawn(Math.floor(
        (orderedNetworks.length - n._2) / 3
      ).toInt)).take(orderedNetworks.length)
  }


}
