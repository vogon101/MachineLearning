package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.core.Sync
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{Display, DisplayMode}

/**
  * GFXManager
  *
  * Created by fredd
  */
class GFXManager(val displaySettings: DisplaySettings) {

  val frameSync = new Sync[() => Unit, Boolean](displaySettings.fpsCap, (f: Option[() => Unit]) => doRender(f.get))

  def fps = frameSync.callsLastSecond

  def render(f: () => Unit): Boolean = frameSync.call(Some(f)) match {
    case Some(x) => x
    case _ => false
  }

  def doRender(f: () => Unit): Boolean = {

    clearScreen()

    f()

    Display.update()

    Display.isCloseRequested

  }

  def init(): Unit = {
    // Initialize OpenGL

    Display.setDisplayMode( new DisplayMode( displaySettings.width, displaySettings.height ) )
    Display.create()
    Display.setTitle( displaySettings.title )
    /*
    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
    glViewport( 0, 0, displaySettings.width, displaySettings.height )

    glMatrixMode( GL_PROJECTION )
    glLoadIdentity()
    glOrtho( 0, Display.getWidth.toDouble, 0, Display.getHeight.toDouble, 1, -1 )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()*/
    glPushAttrib(GL_ALL_ATTRIB_BITS)
    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
    glViewport( 0, 0, displaySettings.width.toInt, displaySettings.height.toInt )

    glMatrixMode( GL_PROJECTION )
    glLoadIdentity()
    glOrtho( 0, Display.getWidth.toDouble, Display.getHeight.toDouble, 0, 1, -1 )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
  }

  def setTitle(t: String) = Display.setTitle(t)

  def clearScreen(): Unit ={
    glClearColor( 0f, 0f, 0f, 1.0f )
    glClear( GL_COLOR_BUFFER_BIT )
  }

}
