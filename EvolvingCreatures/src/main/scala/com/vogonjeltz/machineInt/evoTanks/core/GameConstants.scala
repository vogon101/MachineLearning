package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.gfx.Colour

/**
  * Created by Freddie on 06/08/2017.
  */
object GameConstants {

  //TODO: Find a more elegant solution to this

  val MOVE_SPEED = 5
  val ZOOM_SPEED = 0.01

  val GAME_LAYER = 1
  val FOOD_LAYER = 2

  val TANK_SIZE = 35
  val TANK_STARTING_FOOD = 10d
  val TANK_MAX_SPEED = 1d
  val TANK_CHILD_COST = 15d
  val TANK_CHILD_FAIL_COST = 2d
  val TANK_TICK_COST: Double = 20d/2500d
  val TANK_VISION_RANGE = 400d

  val BRAIN_SIZE = 40
  val BRAIN_ACTIVATION: Double => Double = a => Math.tanh(a)//if (a < -1) -1 else if (a > 1) 1 else a

  val BRAIN_OUT_INDEX_DELTAX  : Int = BRAIN_SIZE - 1
  val BRAIN_OUT_INDEX_DELTAY  : Int = BRAIN_SIZE - 2
  val BRAIN_OUT_INDEX_CHILD   : Int = BRAIN_SIZE - 3
  val BRAIN_OUT_INDEX_SHOOT   : Int = BRAIN_SIZE - 4
  val BRAIN_OUT_INDEX_EAT     : Int = BRAIN_SIZE - 5
  val BRAIN_OUT_INDEX_COLOUR_R: Int = BRAIN_SIZE - 6
  val BRAIN_OUT_INDEX_COLOUR_G: Int = BRAIN_SIZE - 7
  val BRAIN_OUT_INDEX_COLOUR_B: Int = BRAIN_SIZE - 8


  val BRAIN_OUT_THRESH_CHILD  : Double = 0.9
  val BRAIN_OUT_THRESH_SHOOT  : Double = 0.85
  val BRAIN_OUT_THRESH_EAT    : Double = 0.6

  val BULLET_LIFETIME = 700
  val BULLET_COST = 0.3
  val BULLET_SPEED_MULT = 5
  val BULLET_SIZE = 15

  val SIMULATION_MIN_TANKS = 40
  val SIMULATION_MAX_FOOD = 1000
  val SIMULATION_ARENA_WIDTH = 5000
  val SIMULATION_ARENA_HEIGHT = 5000

  val GENERATION_MAX_TIMER = 2500
  val USE_GENERATIONS = true

}
