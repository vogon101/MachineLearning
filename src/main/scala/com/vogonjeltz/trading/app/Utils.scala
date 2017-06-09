package com.vogonjeltz.trading.app

import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.io.File
import javax.imageio.ImageIO

import breeze.linalg.DenseVector
import com.vogonjeltz.trading.lib.StockHistory

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by Freddie on 03/04/2017.
  */
object Utils {

  def readStock(code: String): StockHistory = {
    //println(s"Reading stock $code")
    val bufferedSource = io.Source.fromFile(s"data/$code.csv")
    //(Date, Open, Close)
    val stockHistory: ListBuffer[(String, Double, Double)] = new ListBuffer[(String, Double, Double)]()
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      stockHistory.append((
        cols(0), cols(1).toDouble, cols(4).toDouble
      ))
    }
    bufferedSource.close()
    new StockHistory(code, stockHistory.toList)
  }

  def readMinst(offset: Int = 0, n: Int = 0): List[(Int, DenseVector[Double])] = {

    val bufferedSource = io.Source.fromFile("data/mnist.csv")

    val data: ArrayBuffer[(Int, DenseVector[Double])] = ArrayBuffer()

    val iterator = bufferedSource.getLines().drop(offset)

    for (line <- if (n == 0) iterator else iterator.take(n)) {
      val cols = line.split(",").map(_.trim)

      val label = cols(0).toInt
      val vect = ArrayBuffer[Double]()
      for (i <- Range(0, 28*28)) {
        //TODO: Check that this loads correct data
        vect.append(
          (cols(i + 1).toDouble/255) * 2 - 1d
        )
      }

      data.append((label, DenseVector[Double](vect.toArray)))

    }

    bufferedSource.close()

    data.toList

  }

  def writeText(text: String, path: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(path))
    pw.write(text)
    pw.close()
  }

  def readText(path: String): String = {
    val s = io.Source.fromFile(path)
    s.getLines().toList.mkString("\n")
  }

  def readBWBMP(path: String, size: Int): Array[Double] = {
    println(s"Loading image $path")
    val image = ImageIO.read(new File(path))

    val imageArray = Array.ofDim[Double](size, size)

    val rotatedImage = new BufferedImage(size,size,image.getType())
    val g = rotatedImage.createGraphics()
    g.rotate(Math.toRadians(90), size/2,size/2)

    g.drawImage(image, 0, size, size , -size, null)

    val finalImage = new ArrayBuffer[Double]()

    var xtotal, ytotal = 0d
    var num = 0d

    for (x <- Range(0,size)) {
      for (y <- Range(0, size)) {
        val colour = rotatedImage.getRGB(x, y)
        val p = rotatedImage.getRGB(x, y)
        val a = 255 - ((p >> 24) & 0xff)
        val r = 255 - ((p >> 16) & 0xff)
        val g = 255 - ((p >> 8) & 0xff)
        val b = p & 0xff


        rotatedImage.setRGB(x,y,((a<<24) | (r<<16) | (g<<8) | b))
        xtotal += x * r
        ytotal += y * r
        num += r
      }
    }

    val com_1 = (xtotal/num, ytotal/num)
    println(f"Old Centre of mass: (${com_1._1}%1.2f, ${com_1._2}%1.2f)")

    val translatedImage = new BufferedImage(size,size,image.getType())
    val g_translate = translatedImage.createGraphics()

    val translate = ((14 - com_1._1) /2, (14 - com_1._2)/2)
    println(translate)

    val tx: AffineTransform = new AffineTransform
    tx.translate(translate._1, translate._2)
    g_translate.setTransform(tx)
    g_translate.drawImage(rotatedImage, tx, null)


    xtotal = 0d
    ytotal = 0d
    num = 0d

    for (x <- Range(0,size)) {
      for (y <- Range(0, size)) {
        val colour = translatedImage.getRGB(x, y)
        val r = ((colour & 0xff0000) >> 16) / 255d
        finalImage.append(
          if (r < 0.02) 0
          else r
        )
        xtotal += x * r
        ytotal += y * r
        num += r
      }
    }

    val com_2 = (Math.round(xtotal/num), Math.round(ytotal/num))
    println(f"New Centre of mass: (${com_2._1}, ${com_2._2}%1.2f)")


    //var translatedImage = new BufferedImage(size,size,image.getType())


    //val transform_translate = new AffineTransform()

    //transform_translate.translate((size/2) - com_1._1, (size/2) - com_1._2)

    //val op = new AffineTransformOp(transform_translate, AffineTransformOp.TYPE_BILINEAR);

    //translatedImage = op.filter(translatedImage, null)

    //println(translatedImage.getMinTileX)
    //println(translatedImage.getWidth)

    //println(translatedImage.getMinTileY)
    //println(translatedImage.getHeight)

    finalImage.toArray

  }

}
