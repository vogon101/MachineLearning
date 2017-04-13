package com.vogonjeltz.tradingnetworks.app

import com.vogonjeltz.tradingnetworks.lib.networks.evolution.EvolutionCoordinator
import com.vogonjeltz.tradingnetworks.lib.networks.{Matrix, NeuralNetwork, Vect}
import com.vogonjeltz.tradingnetworks.lib.networks.{NeuralNetwork, Vect}

import scala.util.Random

/**
  * Created by Freddie on 29/03/2017.
  */
object Tests  {

  val ec = new EvolutionCoordinator(
    (gen, i,o) => {
      0
      //Math.abs(1/i.diff(o))
    },
    () => {
      val matrix = Matrix.ofDim(100,100)((x: Int, y: Int) => Random.nextDouble())

      new NeuralNetwork(matrix, (a: Double) =>
        if (a <= 0) 0
        else if (a > 1) 1
        else 1 - Math.sin(a * Math.PI) / (a * Math.PI)
      )
    },
    g => c => new Vect(Vect.ZERO(90).data ::: List(0.1,0.1,0.1,0.1,0.8,0,0.8,0,0.8,0))
  )

  ec.run(2000)

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
