package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.gfx.{Colour, Renderable}
import com.vogonjeltz.machineInt.evoTanks.physics.collision.Collideable

/**
  * SimulationObject
  *
  * Created by fredd
  */
abstract class SimulationObject extends Collideable with Renderable {

  def name: String = "SimulationObject"

  /**
    * The update function is allowed to affect its own simulation object only
    * Interactions with other objects MUST involve emitting an Action
    * @return
    */
  def update(): List[Action]

  def colour: Colour = Colour.WHITE


}
