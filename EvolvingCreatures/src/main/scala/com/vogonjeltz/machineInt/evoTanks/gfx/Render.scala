package com.vogonjeltz.machineInt.evoTanks.gfx

import java.awt.Font

import com.vogonjeltz.machineInt.evoTanks.physics.Vect
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.{Color, TrueTypeFont}

import scala.collection.immutable.HashMap

/**
  * Render
  *
  * Created by fredd
  */
object Render {

  private var _offset: Vect = Vect.ZERO
  private var _zoom: Vect = Vect(1,1)

  //TODO: Should the frame know about colour
  def withContext( frame: Frame, translate: Boolean = true )( actions: => Unit ): Unit = {

    glPushMatrix()

    val colourBuffer = BufferUtils.createFloatBuffer( 16 )
    glGetFloat( GL_CURRENT_COLOR, colourBuffer )

    if (translate) {
      glTranslated(_offset.x, _offset.y, 0)
      glScaled(zoom.x, zoom.y, 1)
    }

    for (p <- frame.position) glTranslated(p.x, p.y, 0)
    for (r <- frame.rotation) glRotated(r.toDeg, 0, 0, 1)
    for (c <- frame.colour)   glColor3d(c.r, c.g, c.b)

    actions

    glPopMatrix()
    glColor3d( colourBuffer.get( 0 ).toDouble, colourBuffer.get( 1 ).toDouble, colourBuffer.get( 2 ).toDouble )

  }

  def drawText( text: List[String], awtFont: Font, translate: Boolean = false, interline: Double = 1.5, frame: Frame = Frame() ) = {
    val font = getTTFont( awtFont )

    withContext( frame, translate ) {
      // Slick fonts don't work like OpenGL. I retrieve the current colour from the OpenGL
      val colourBuffer = BufferUtils.createFloatBuffer( 16 )
      glGetFloat( GL_CURRENT_COLOR, colourBuffer )
      val defaultTextColour = Colour( colourBuffer.get( 0 ).toDouble, colourBuffer.get( 1 ).toDouble, colourBuffer.get( 2 ).toDouble )
      val color = frame.colour.getOrElse( defaultTextColour )
      val slickColor = new Color( color.r.toFloat, color.g.toFloat, color.b.toFloat, 1 )

      // Draw all the lines of text
      text.zipWithIndex.foreach {
        case ( t, i ) ⇒
          font.drawString( 0, ( awtFont.getSize * interline * i ).toFloat, t, slickColor )
      }
    }
  }

  private var fontMap = HashMap[Int, TrueTypeFont]()

  def clearFontCache(): Unit = fontMap = HashMap()

  private def getTTFont( awtFont: Font ): TrueTypeFont = {
    if ( !fontMap.contains( awtFont.hashCode ) ) {
      val ttfont = new TrueTypeFont( awtFont, false )

      fontMap = fontMap + ( awtFont.hashCode -> ttfont )
      return ttfont
    }

    fontMap( awtFont.hashCode )
  }

  def textContext()( actions: ⇒ Unit ): Unit = {
    // Enable alpha blending to merge text and graphics
    glEnable( GL_BLEND )
    glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA )

    actions

    glDisable( GL_BLEND )
  }

  def point(vect: Vect): Unit = {
    glVertex2d(vect.x, vect.y)
  }

  def translateOffset(v: Vect):Unit = _offset += v
  def setOffset(v:Vect) = _offset = v
  def offset = _offset

  def setZoom(s: Double): Unit = _zoom = Vect(s,s)
  def changeZoom(s: Double): Unit = _zoom += Vect(s,s)
  def zoom = _zoom
  def clearZoom(): Unit = _zoom = Vect(1,1)

}
