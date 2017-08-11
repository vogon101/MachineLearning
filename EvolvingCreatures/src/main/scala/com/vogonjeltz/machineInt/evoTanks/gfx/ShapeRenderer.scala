package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Circle, Shape}
import org.lwjgl.opengl.GL11._

/**
  * ShapeRenderer
  *
  * Created by fredd
  */
object ShapeRenderer {

  def render(shape: Shape, params: RenderParams = RenderParams()) = shape match {
    case shape: Box => renderBox(shape, params)
    case shape: Circle => renderCircle(shape, params)
  }

  def renderBox(shape: Box, params:RenderParams) = {

    val frame: Frame = Frame(_colour = params.colour)

    if (params.filled) {
      Render.withContext(frame) {
        glBegin(GL_QUADS)
        Render.point(shape.topLeft)
        Render.point(shape.topRight)
        Render.point(shape.bottomRight)
        Render.point(shape.bottomLeft)
        glEnd()
      }
    }
    else {
      Render.withContext(frame) {
        glBegin(GL_LINE_LOOP)
        Render.point(shape.topLeft)
        Render.point(shape.topRight)
        Render.point(shape.bottomRight)
        Render.point(shape.bottomLeft)
        glEnd()
      }
    }

  }

  def renderCircle(shape: Circle, params: RenderParams) = {
    val CIRCLE_TRIANGLES = 20
    val frame: Frame = Frame(shape.position, params.colour)
    if (params.filled)
      Render.withContext(frame) {
        var x2, y2:Double = 0
        glBegin(GL_TRIANGLE_FAN)
        for (angle <- 0d to (2d * Math.PI) by (2d * Math.PI / CIRCLE_TRIANGLES)) {
          x2 = Math.sin(angle)*shape.radius
          y2 = Math.cos(angle)*shape.radius
          glVertex2d(x2,y2)
        }
        glEnd()
      }
    else
      Render.withContext(frame) {
        glBegin(GL_LINE_LOOP)
        for(i <- 0 to 360 by 5){
          val deginrad = i * Math.PI / 180
          glVertex2d(Math.cos(deginrad) * shape.radius, Math.sin(deginrad) * shape.radius)
        }
        glEnd()
      }
  }

}

case class RenderParams (
                        filled: Boolean = false,
                        colour: Colour = Colour.WHITE
                        )

