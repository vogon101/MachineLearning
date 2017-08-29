package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.physics._
import org.lwjgl.opengl.GL11._

/**
  * ShapeRenderer
  *
  * Created by fredd
  */
object ShapeRenderer {

  //def renderBoxes(params: RenderParams)(shapes: Box*): Unit = renderBoxes(params, shapes)

  def renderBox(params:RenderParams, shape: Box): Unit = {

    val frame: Frame = Frame(_colour = params.colour, _rotation = params.rotation, _position = params.offset)

    Render.withContext(frame, params.translate) {
      glBegin(if (params.filled) GL_QUADS else GL_LINE_LOOP)
        Render.point(shape.topLeft)
        Render.point(shape.topRight)
        Render.point(shape.bottomRight)
        Render.point(shape.bottomLeft)
      glEnd()
    }

  }

  //def renderCircles(params: RenderParams)(circles: Circle*): Unit = renderCircles(params, circles)

  def renderCircle(params: RenderParams, shape: Circle): Unit = {
    val CIRCLE_TRIANGLES = 20
    val frame: Frame = Frame(_colour = params.colour, _rotation = params.rotation, _position = params.offset)

    Render.withContext(frame, params.translate) {
        glBegin(if (params.filled) GL_TRIANGLE_FAN else GL_LINE_LOOP)
          for (angle <- 0d to (2d * Math.PI) by (2d * Math.PI / CIRCLE_TRIANGLES))
            glVertex2d(
              Math.sin(angle)*shape.radius + shape.position.x,
              Math.cos(angle)*shape.radius + shape.position.y
            )
        glEnd()
    }
  }

  def renderLine(params: RenderParams, shape: LineSegment): Unit = {
    val frame: Frame = Frame(_colour = params.colour, _rotation = params.rotation, _position = params.offset)
    Render.withContext(frame, params.translate) {

      glBegin(GL_LINES)
      Render.point(shape.p1)
      Render.point(shape.p2)
      glEnd()

    }
  }

  //def renderTriangles(params: RenderParams)(triangles: Triangle*): Unit = renderTriangles(params, triangles)

  def renderTriangle(params: RenderParams, shape: Triangle): Unit = {

    val frame: Frame = Frame(_colour = params.colour, _rotation = params.rotation, _position = params.offset)

    Render.withContext(frame, params.translate) {
      glBegin(if (params.filled) GL_TRIANGLES else GL_LINE_LOOP)
        Render.point(shape.p1)
        Render.point(shape.p2)
        Render.point(shape.p3)
      glEnd()
    }

  }

}

case class RenderParams (
  filled: Boolean = false,
  colour: Colour = Colour.WHITE,
  rotation: Rotation = Deg(0),
  translate: Boolean = true,
  offset: Vect = null
) {



}

