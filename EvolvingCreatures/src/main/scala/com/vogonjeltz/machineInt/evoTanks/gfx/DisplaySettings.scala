package com.vogonjeltz.machineInt.evoTanks.gfx

/**
  * DisplaySettings
  *
  * Created by fredd
  */
class DisplaySettings {


  val height: Int = 800//640
  val width : Int = 800//(height/9)*16

  val updateSpeed: Int = 60
  val fpsCap: Int = 30

  protected var _title = "SimpleGamee"

  def title = _title

  def setTitle(t: String) = _title = t

  val enabled: Boolean = true

}

