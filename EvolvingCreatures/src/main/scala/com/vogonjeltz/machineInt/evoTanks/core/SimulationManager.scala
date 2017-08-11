package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.UX.InputManager
import com.vogonjeltz.machineInt.evoTanks.gfx._
import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Vect}
import com.vogonjeltz.machineInt.evoTanks.physics.collision.{CollisionManager, QuadTree}
import com.vogonjeltz.machineInt.evoTanks.simulation.Tank

import scala.collection.mutable.ArrayBuffer

/**
  * SimulationManager
  *
  * Created by fredd
  */
class SimulationManager {

  val arena: Arena = new Arena(5000, 5000)

  lazy val renderManager: GFXManager = new GFXManager(new DisplaySettings)
  lazy val inputManager: InputManager = new InputManager

  var renderCount = 0

  var running = true

  def run(): Unit = {

    //arena.objects.append(new Tank(Vect(100, 100)))

    renderManager.init()

    while (running) {

      arena.update(() => {

      })

      running = !renderManager.render(() => {

        arena.collisionManager.debugRender()

        arena.objects.foreach (X => {
          X.render()
        })

        if (renderCount % 10 == 0)
          renderManager.setTitle(
              s"Tanks |" +
              f" UPS: ${arena.updateSync.callsLastSecond} (Max ${arena.updateSync.rate}, Sloppy: ${arena.updateSync.timingModifier}%.2f) | " +
              f"FPS: ${renderManager.frameSync.callsLastSecond} (Max ${renderManager.frameSync.rate}, Sloppy: ${renderManager.frameSync.timingModifier}%.2f) | " +
              f"Tanks: ${arena.objects.count(_.isInstanceOf[Tank])}")
        renderCount += 1

      })

      inputManager.poll { inputResults =>
        Render.translateOffset(Vect(
          GameConstants.MOVE_SPEED * inputResults.xOffsetDelta,
          GameConstants.MOVE_SPEED * inputResults.yOffsetDelta
        ))

        Render.changeZoom(inputResults.zoomLevelDelta * GameConstants.ZOOM_SPEED)

        if (arena.updateSync.rate + inputResults.UPSDelta * 5 > 0 && inputResults.UPSDelta != 0)
          arena.updateSync.setRate(arena.updateSync.rate + inputResults.UPSDelta * 5)
      }

    }

  }

}
