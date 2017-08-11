package com.vogonjeltz.machineInt.evoTanks.physics.collision

import com.vogonjeltz.machineInt.evoTanks.physics.Shape
import com.vogonjeltz.machineInt.evoTanks.simulation.Tank

import scala.util.Random

/**
  * Collideable
  *
  * Created by fredd
  */
trait Collideable {

  val UUID = Random.nextDouble() * Random.nextInt(1000000)
  def > (t: Collideable) = UUID > t.UUID

  def shape: Shape

  val layer: Int

}
