package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.UX.InputManager
import com.vogonjeltz.machineInt.evoTanks.gfx._
import com.vogonjeltz.machineInt.evoTanks.networks.NeuralNetwork
import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Vect}
import com.vogonjeltz.machineInt.evoTanks.physics.collision.{CollisionManager, QuadTree}
import com.vogonjeltz.machineInt.evoTanks.simulation.{Tank, TankBrain}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * SimulationManager
  *
  * Created by fredd
  */
class SimulationManager {

  private val _runCode = System.currentTimeMillis().toString

  private var _arena: Arena = new Arena(GameConstants.USE_GENERATIONS, runCode = _runCode)
  def arena: Arena = _arena

  lazy val renderManager: GFXManager = new GFXManager(new DisplaySettings)
  lazy val inputManager: InputManager = new InputManager

  var renderCount = 0

  var running = true

  var genCount = 0

  def run(): Unit = {

    //arena.objects.append(new Tank(Vect(100, 100)))

    renderManager.init()

    while (running) {

      arena.update(() => {

      })

      if (GameConstants.USE_GENERATIONS && arena.UPDCount > GameConstants.GENERATION_MAX_TIMER) {
        println("-------------------------------")
        println(s"Generation: $genCount")
        genCount += 1

        println(s"Dead tanks: ${arena.deadTanksSociety.length}")
        val parents = (arena.tanks ++ arena.deadTanksSociety.filter(_.food > 10).sortBy(1d/_.score).take(1)).filter(_.score > 0).sortBy(1d /_.score).take(10)

        println(s"Average score: ${parents.map(_.score).sum / parents.length}")
        println(s"Generation average: ${parents.map(_.generation.toDouble).sum / parents.length}")
        println(s"Generation Max: ${parents.map(_.generation.toDouble).max}")
        println(s"${parents.count(_.isDead)} dead")
        val h = parents.head
        println(s"Total Food: ${h.totalFoodEaten / 200d} | Food: ${h.food / 50d} | Children : ${h.totalChildren} | Kills : ${h.totalKills * (if(!h.isDead) 100 else 4)} | Lifespan: ${h.lifeSpan / 1000d} | Total shots: ${h.totalShots / 300d} (GAge : ${h.generation}, Dead: ${h.isDead})")
        println(s"Giving ${parents.head.score}")

        def randomPos = Vect(Random.nextInt(GameConstants.SIMULATION_ARENA_WIDTH - 200) + 100, Random.nextInt(GameConstants.SIMULATION_ARENA_HEIGHT - 200) + 100)

        val newTanks = parents.zipWithIndex.flatMap {
          case (t: Tank,i: Int) =>
            val filtered = parents.filter(_ != t)
            for (j <- Range(0, 10-i))
              yield
                (NeuralNetwork.cross(
                  t.brain.network,
                  filtered(Random.nextInt(filtered.length - 1)).brain.network
                ), t)
        }.map { N =>
          new Tank(
            randomPos,
            new TankBrain(N._1), 0, N._2.generation + 1
          )
        } ++ parents.filter(!_.isDead).take(5).map(P => new Tank(randomPos, P.brain, 0, P.generation + 1)) ++ Range(0,5).map(X => Tank.spawn(randomPos, 0))


        /*val newTanks = parents.zipWithIndex.flatMap { T =>
          T._1.brain.network.spawn(10 - T._2).map(X => (X, T._1))
        }.map { N =>
          new Tank(
            Vect(Random.nextInt(GameConstants.SIMULATION_ARENA_WIDTH - 200) + 100, Random.nextInt(GameConstants.SIMULATION_ARENA_HEIGHT - 200) + 100),
            new TankBrain(N._1), 0, N._2.generation + 1
          )
        }*/

        var prevRate = arena.updateSync.rate

        _arena = new Arena(
          true,
          arena.genCount + 1,
          _runCode,
          newTanks
        )

        arena.updateSync.setRate(prevRate)

      }

      running = !renderManager.render(() => {


        if (arena.updateSync.rate != -1) {
          //arena.collisionManager.debugRender()

          RenderPrimitive.batchShapes((arena.objects ++ arena.tanks.flatMap(_.visionRays)).flatMap(_.render()))
        }


        if (renderCount % 10 == 0)
          renderManager.setTitle(
              s"Tanks |" +
              f" UPS: ${arena.updateSync.callsLastSecond} (${arena.updateSync.callsLastSecond/60d}%.2fx, Max ${arena.updateSync.rate}, Sloppy: ${arena.updateSync.timingModifier}%.2f) | " +
              f"FPS: ${renderManager.frameSync.callsLastSecond} (Max ${renderManager.frameSync.rate}, Sloppy: ${renderManager.frameSync.timingModifier}%.2f) | " +
              f"Tanks: ${arena.objects.count(_.isInstanceOf[Tank])} | " +
              f"Ticks: ${arena.UPDCount}")
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

        if (inputResults.didRemoveCap) {
          arena.updateSync.setRate(
            if (arena.updateSync.rate == -1) 60 else -1
          )
        }
      }

    }

    arena.cleanUp()

  }

}
