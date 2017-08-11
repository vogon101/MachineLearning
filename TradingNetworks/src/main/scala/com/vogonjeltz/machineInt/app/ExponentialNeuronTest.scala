package com.vogonjeltz.machineInt.app

import scala.collection.immutable.Range
import scala.util.Random

/**
  * Created by Freddie on 15/06/2017.
  */
object ExponentialNeuronTest{

  class LayeredNetwork[T]
      (
        val layers: Array[Layer[T]]
      )
  {

    def compute(in: Array[Double]) = {
      var lastResult = in
      for (l <- layers) {
        lastResult = l.compute(lastResult)
      }
      lastResult
    }

    def spawn(n: Int): Array[LayeredNetwork[T]] = {
      (for (i <- Range(0, n))
        yield new LayeredNetwork[T](
          layers.map(_.spawn)
        )).toArray
    }

  }

  abstract class Layer[T] {

    val nNeurones: Int

    /**
      * One for each neuron (nOut) * One for each input (nIn)
      */
    val inputParams: Array[Array[T]]

    var state: Array[Double] = Array.fill(nNeurones)(0)

    def activate(acc: Double) : Double

    def compute(inputs: Array[Double]): Array[Double]

    def spawn: Layer[T]

  }

  trait SoftPlusLayer[T] extends Layer[T] {

    override def activate(acc: Double) = Math.log(1 + Math.pow(Math.E, acc))

  }

  trait RELULayer[T] extends Layer[T] {

    override def activate(acc: Double) = if (acc > 0) acc else 0

  }

  trait LinearLayer[T] extends Layer[T] {

    override def activate(acc: Double) = acc

  }

  class ExponentialLayer(
                        override val nNeurones: Int,
                        override val inputParams: Array[Array[(Double, Double)]]
                        ) extends Layer[(Double, Double)] with SoftPlusLayer[(Double, Double)] {

    override def compute(inputs: Array[Double]): Array[Double] = {
      for (neuron <- Range(0, nNeurones)) {
        var acc = 0d
        for (input <- inputs.indices) {
          acc += Math.pow(inputs(input), inputParams(neuron)(input)._2) * inputParams(neuron)(input)._1
        }
        state(neuron) = activate(acc)
      }
      state
    }

    override def spawn: ExponentialLayer = new ExponentialLayer(
      nNeurones,
      inputParams.map(
        _.map(X =>
          (X._1 + (Random.nextDouble() / 10) - 0.05, X._2 + (Random.nextDouble() / 10) - 0.05)
        )
      )
    )

  }



  def main(args: Array[String]): Unit = {
    val a = 2
    val b = 3
    val c = 3

    def newNet() = {
      val layer1 = new ExponentialLayer(
        nNeurones = 1,
        inputParams = Array(Array((Random.nextDouble(),Random.nextDouble())))
      )

      val layer2 = new ExponentialLayer(
        nNeurones = 3,
        inputParams = Array(Array((Random.nextDouble(),Random.nextDouble())),Array((Random.nextDouble(),Random.nextDouble())),Array((Random.nextDouble(),Random.nextDouble())))
      )

      val layer3 = new ExponentialLayer(
        nNeurones = 1,
        inputParams = Array(Array((Random.nextDouble(),Random.nextDouble()), (Random.nextDouble(),Random.nextDouble()), (Random.nextDouble(),Random.nextDouble())))
      )

      new LayeredNetwork[(Double, Double)](Array(layer1, layer2, layer3))

    }

    var nets = Range(0, 30).toArray.map(X => newNet())
    var bestNet = nets.head



    def value(x: Double) ={
      a * Math.pow(x, 2) + b * x + c
    }

    for (i <- Range(0, 10000)) {
      if (i % 100 == 0) println(i)
      val overall_scores = Array.tabulate(nets.length)(X => 0d)
      for (i <- Range(0, 1000)) {
        val x = Random.nextDouble() * 40
        val realValue = value(x)
        val outputs = nets.map(_.compute(Array(x)).head)
        val scores = outputs.map(X => 1/ Math.pow(X - realValue, 2)).zipWithIndex.map(X => if (X._1.isNaN || X._1.isInfinite) (-100000d, X._2) else X)
        for ((s,i) <- scores) {
          overall_scores(i) = overall_scores(i) + s
        }
      }

      val parents = overall_scores.zipWithIndex.sortWith((X,Y) => X._1 > Y._1).take(5)

      bestNet = nets(parents.head._2)
      nets = parents.take(5).map(X => nets(X._2)).flatMap(_.spawn(5)) ++: Range(0, 5).map(X => newNet()).toArray


    }

    for (i <- bestNet.layers(1).inputParams)
      println(s"w = ${i.head._1}, n = ${i.head._2}")

    for (i <- Range(0, 40)) {
      println(s"x = $i -> ${bestNet.compute(Array(i)).head} (Expected ${value(i)})")
    }





  }
}