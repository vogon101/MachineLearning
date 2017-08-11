package com.vogonjeltz.machineInt.evoTanks.UX

import com.vogonjeltz.machineInt.evoTanks.core.Sync
import org.lwjgl.input.Keyboard

/**
  * Created by Freddie on 06/08/2017.
  */
class InputManager {

  val inputSync = new Sync[InputResults => Unit, Unit](45, (f: Option[InputResults => Unit]) => doInput(f.get))

  def poll(f: InputResults => Unit): Unit = inputSync.call(Some(f))

  def doInput(f: InputResults => Unit): Unit = {

    val results = new InputResults

    if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
      results.xOffsetDelta += 1
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
      results.xOffsetDelta -= 1
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
      results.yOffsetDelta -= 1
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
      results.yOffsetDelta += 1
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
      results.zoomLevelDelta -= 1
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
      results.zoomLevelDelta += 1
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_LBRACKET)) {
      results.UPSDelta -= 1
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_RBRACKET)) {
      results.UPSDelta += 1
    }

    f(results)

  }

}

class InputResults {

  var xOffsetDelta: Double = 0
  var yOffsetDelta: Double = 0

  var zoomLevelDelta: Double = 0

  var UPSDelta: Int = 0

}
