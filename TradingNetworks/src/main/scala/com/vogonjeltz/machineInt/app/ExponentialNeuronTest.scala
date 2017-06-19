package com.vogonjeltz.machineInt.app

import scala.collection.immutable.Range

/**
  * Created by Freddie on 15/06/2017.
  */
object ExponentialNeuronTest{

  abstract class Layer[T] {

    val nNeurones: Int

    /**
      * One for each neuron (nOut) * One for each input (nIn)
      */
    val inputParams: Array[Array[T]]
    val biases: Array[Double]

    var state: Array[Double] = Array.fill(nNeurones)(0)

    def activate(acc: Double) : Double

    def compute(inputs: Array[Double]): Array[Double]

  }

  trait RELULayer[T] extends Layer[T] {

    override def activate(acc: Double) = if (acc > 0) acc else 0

  }

  trait LinearLayer[T] extends Layer[T] {

    override def activate(acc: Double) = acc

  }

  class ExponentialLayer(
                        override val nNeurones: Int,
                        override val biases: Array[Double],
                        override val inputParams: Array[Array[(Double, Double)]]
                        ) extends Layer[(Double, Double)] with LinearLayer[(Double, Double)] {

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

  }


  def main(args: Array[String]): Unit = {
    val a = 2
    val b = 3
    val c = 3

    val layer1 = new ExponentialLayer(
      nNeurones = 1,
      biases = Array(1d),
      inputParams = Array(Array((1,1)))
    )

    val layer2 = new ExponentialLayer(
      nNeurones = 3,
      biases = Array(1,1,1),
      inputParams = Array(Array((a,2)),Array((b,1)),Array((c,0)))
    )

    val layer3 = new ExponentialLayer(
      nNeurones = 1,
      biases = Array(1),
      inputParams = Array(Array((1,1), (1,1), (1,1)))
    )

    def compute(x: Double) ={
      val l1o = layer1.compute(Array(x))
      val l2o = layer2.compute(l1o)
      val l3o = layer3.compute(l2o)

      val ex = a * Math.pow(x, 2) + b * x + c

      println(s"$x - > ${l3o.head} (expected. $ex)")

    }

    for (i <- Range(-10,10)) {
      compute(i)
    }

  }
}