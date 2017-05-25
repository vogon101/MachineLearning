package com.vogonjeltz.trading.lib.networks

import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import breeze.linalg.{DenseMatrix, DenseVector}
import com.vogonjeltz.trading.lib.networks.evolution.EvolutionCoordinator
import net.liftweb.json.JsonAST.JObject

/**
  * NetworkStorage
  *
  * Created by fredd
  */
object NetworkStorage {

  implicit val formats = DefaultFormats

  def evolutionCoordinatorSnapshot(ec: EvolutionCoordinator): JObject = {
    ("params" ->
        ("generationNumber" -> ec.lastGeneration) ~
        ("lastAverageFitness" -> ec.lastAverageFitness) ~
        ("bestNetwork" ->
            ("fitness" -> ec.best.get._2) ~
            ("network" -> networkRepresentation(ec.best.get._1))
        )
    ) ~ ("parent" -> networkRepresentation(ec.lastBestNetwork))
  }

  def networkRepresentation(nn: NeuralNetwork): JObject = {
      ("inputWeights" -> denseVectorRepresentation(nn.inputWeights)) ~
      ("outputWeights" -> denseVectorRepresentation(nn.outputWeights)) ~
      ("connections" -> denseMatrixRepresentation(nn.connections))
      //activation function not stored
  }

  def denseVectorRepresentation(dv: DenseVector[Double]): JObject =
    ("size" -> dv.length) ~
    ("elements" -> dv.toArray.toList)

  def denseMatrixRepresentation(dm: DenseMatrix[Double]): JObject =
    ("size" -> List(dm.rows, dm.cols)) ~
    ("elements" -> dm.toArray.toList)

  def readToEvolutionCoordinator(representation: String, evolutionCoordinator: EvolutionCoordinator) = {
    println("Reading saved trained networks")
    val parsed = parse(representation)

    evolutionCoordinator.lastGeneration = (parsed \ "params" \ "generationNumber").extract[Int]
    evolutionCoordinator.lastAverageFitness = (parsed \ "params" \ "lastAverageFitness").extract[Double]

    val parent = readNetwork(parsed \ "parent", evolutionCoordinator.defaultActivationFunction)

    evolutionCoordinator.population = parent.spawn(evolutionCoordinator.settings.populationSize, 5)
    evolutionCoordinator.best = Some((readNetwork(parsed \ "params" \ "bestNetwork" \ "network", evolutionCoordinator.defaultActivationFunction), (parsed \ "params" \ "bestNetwork" \ "fitness").extract[Double]))
    evolutionCoordinator.lastBestNetwork = parent


    println(s"Loaded best network fitness: ${evolutionCoordinator.best.get._2}")

  }

  def readNetwork(network: JValue, activationFunction: Double => Double): NeuralNetwork = {
    new NeuralNetwork(
      readDenseMatrix(network \ "connections"),
      readDenseVector(network \"inputWeights"),
      readDenseVector(network \ "outputWeights"),
      activationFunction
    )
  }

  def readDenseVector(vector: JValue): DenseVector[Double] = {
    DenseVector(
      (vector \ "elements")
        .extract[List[Double]]
        .toArray
    )
  }

  def readDenseMatrix(matrix: JValue): DenseMatrix[Double] = {
    val size = (matrix \ "size").asInstanceOf[JArray].arr.map(_.asInstanceOf[JInt].num.toInt)
    new DenseMatrix[Double](size.head, size.last, (matrix \ "elements").extract[List[Double]].toArray)
  }


}