package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Circle, LineSegment, Triangle}

/**
  * RenderPrimitive
  * TODO: Replace ShapeRenderer with this system?
  *
  * Created by fredd
  */
sealed abstract class RenderPrimitive(val renderParams: RenderParams)

case class CircleRenderer(circle: Circle, override val renderParams: RenderParams = RenderParams()) extends RenderPrimitive (renderParams)
case class LineSegmentRenderer(lineSegment: LineSegment, override val renderParams: RenderParams = RenderParams()) extends RenderPrimitive (renderParams)
case class BoxRenderer(box: Box, override val renderParams: RenderParams = RenderParams()) extends RenderPrimitive (renderParams)
case class CustomRenderer(f: () => Unit, override val renderParams: RenderParams = RenderParams()) extends RenderPrimitive (renderParams)
case class TriangleRenderer(triangle: Triangle, override val renderParams: RenderParams = RenderParams()) extends RenderPrimitive (renderParams)

object RenderPrimitive {

  def render(primitive: RenderPrimitive, translate: Option[Boolean] = None): Unit = {

    val params = translate.map(T => primitive.renderParams.copy(translate = T)).getOrElse(primitive.renderParams)

    primitive match {
      case c: CircleRenderer => ShapeRenderer.renderCircle(params, c.circle)
      case l: LineSegmentRenderer => ShapeRenderer.renderLine(params, l.lineSegment)
      case b: BoxRenderer => ShapeRenderer.renderBox(params, b.box)
      case c: CustomRenderer => c.f()
      case t: TriangleRenderer => ShapeRenderer.renderTriangle(params, t.triangle)
    }

  }

  def batchShapes(primitives: Seq[RenderPrimitive]): Unit = {

    Render.withTranslate {
      primitives.foreach { P =>
        render(P, Some(false))
      }
    }

  }


  //TODO: Im to tired to code so when you wake up this is what you need to do
  //1) Render primitives needs to stop just shadowing ShapeRenderer
  //2) Need to support MUCH better batching:
  //  a) Need to be able to batch shapes of different colours
  //  b) This requires reworking the render class
  //3) IMPORTANT: DO NOT FORGET:
  //  a) MUST NOT GO BACK TO HAVING A STATEFUL SETUP - THIS IS BAD
  //  b) WE CANNOT JUST BE RE-CREATING OPENGL
  //  c) The functions must not assume anything
  //4) Maybe some sort of shape->List[Vect]



  //def batchTriangles(primitives: Seq)


}