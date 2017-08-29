package com.vogonjeltz.machineInt.evoTanks.simulation

import breeze.linalg.DenseVector
import com.vogonjeltz.machineInt.evoTanks.core.GameConstants
import com.vogonjeltz.machineInt.evoTanks.gfx.Colour
import com.vogonjeltz.machineInt.evoTanks.networks.NeuralNetwork

/**
  * TankBrain
  *
  * Created by fredd
  */
class TankBrain(val network: NeuralNetwork) {

  def update(in: DenseVector[Double]): TankBrainOutput = {

    network.tick(in)
    val out = network.output
    TankBrainOutput (
      out(GameConstants.BRAIN_OUT_INDEX_DELTAX),
      out(GameConstants.BRAIN_OUT_INDEX_DELTAY),
      Math.abs(out(GameConstants.BRAIN_OUT_INDEX_SHOOT))  > GameConstants.BRAIN_OUT_THRESH_SHOOT,
      Math.abs(out(GameConstants.BRAIN_OUT_INDEX_CHILD))  > GameConstants.BRAIN_OUT_THRESH_CHILD,
      Math.abs(out(GameConstants.BRAIN_OUT_INDEX_EAT))    > GameConstants.BRAIN_OUT_THRESH_EAT,
      Colour(
        out(GameConstants.BRAIN_OUT_INDEX_COLOUR_R),
        out(GameConstants.BRAIN_OUT_INDEX_COLOUR_G),
        out(GameConstants.BRAIN_OUT_INDEX_COLOUR_B)
      )
    )

  }

}

case class TankBrainOutput(
  deltaX: Double,
  deltaY: Double,
  shoot: Boolean,
  child: Boolean,
  eat: Boolean,
  colour: Colour
)