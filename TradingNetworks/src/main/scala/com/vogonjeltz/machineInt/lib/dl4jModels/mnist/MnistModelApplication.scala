package com.vogonjeltz.machineInt.lib.dl4jModels.mnist

import com.vogonjeltz.machineInt.lib.Utils
import com.vogonjeltz.machineInt.lib.dl4jModels.MultiLayerModelApplication
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.cpu.nativecpu.NDArray

/**
  * Created by Freddie on 09/06/2017.
  */
class MnistModelApplication (override val model: MultiLayerNetwork) extends MultiLayerModelApplication[Int] {

  override def use(input: INDArray) = use(input, false)

  def use(input: INDArray, log: Boolean): (Int, Array[Double]) = {
    val output = model.output(input).dup.data.asDouble
    var bestGuess = (-1d, -1)
    for ((x, i) <- output.zipWithIndex) {
      if (log) println(f"$i - $x%1.2f")
      if (x > bestGuess._1) bestGuess = (x, i)
    }
    (bestGuess._2, output)
  }

  def use(pathToImage: String) : (Int, Array[Double])= {
    val imageData = Utils.readBWBMP(pathToImage, 28)
    val input = new NDArray(imageData.map(_.toFloat))
    use(input)
  }

}

object MnistModelApplication {


}
