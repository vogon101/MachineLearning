package com.vogonjeltz.machineInt.evoTanks.app

import com.vogonjeltz.machineInt.evoTanks.core.SimulationManager
import com.vogonjeltz.machineInt.evoTanks.physics._
import com.vogonjeltz.machineInt.evoTanks.simulation.Tank

import scala.util.Random

/**
  * EvoTanksApp
  *
  * Created by fredd
  */
object EvoTanksApp extends App {

  println("Running EvoTanks")

  val manager = new SimulationManager
  implicit val arena = manager.arena

  for (i <- Range(0, 30)) {
    val t1 = new Tank(Vect(Random.nextInt(5000), Random.nextInt(5000)))
    t1._velocity = Vect(Random.nextDouble()*2 - 1, Random.nextDouble()*2 - 1)

    manager.arena.objects.append(t1)
  }

  manager.run()


}
