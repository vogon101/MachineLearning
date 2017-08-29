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


  val l1 = LineSegment(Vect(0,0), Vect(100, 100))
  val l2 = LineSegment(Vect(100,0), Vect(0, 100))
  println(l1.intersects(l2))

  manager.run()


}
