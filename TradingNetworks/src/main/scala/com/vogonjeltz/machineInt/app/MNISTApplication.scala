package com.vogonjeltz.machineInt.app

import javax.imageio.IIOException

import com.vogonjeltz.machineInt.app.MNISTApplication.seed
import com.vogonjeltz.machineInt.lib.Serialise
import com.vogonjeltz.machineInt.lib.dl4jModels.mnist.{MnistModelApplication, MnistModelDef}
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator

/**
  * Created by Freddie on 09/06/2017.
  */
object MNISTApplication {

  val modelPath = "model/mnist/v1.zip"
  val seed = 100

  def main(args: Array[String]): Unit = {

    val model = Serialise.read(modelPath)

    val app = new MnistModelApplication(model)
    while(true){
      try{
        val o = app.use("custom-mnist/" + readLine(">") + ".bmp")
        println(s"Prediction = ${o._1}, Confidence = ${o._2(o._1)}")
      } catch {
        case e: IIOException => println("Could not open file")
      }
    }
  }



}

object MNISTApplication_InitModel extends App {


  val batchSize = 200


  val mnistTrain = new MnistDataSetIterator(batchSize, true, MNISTApplication.seed)
  val mnistTest = new MnistDataSetIterator(batchSize, false, MNISTApplication.seed)

  val definition = new MnistModelDef(seed)
  val model = definition.createModel()

  val app = new MnistModelApplication(model)
  app.doTraining(MNISTApplication.modelPath, mnistTrain, 3, 6)
  println(app.evaluate(testData = mnistTest, 10))

}

object MNISTApplication_MoreTrain extends App {

  val batchSize = 200


  val mnistTrain = new MnistDataSetIterator(batchSize, true, MNISTApplication.seed)
  val mnistTest = new MnistDataSetIterator(batchSize, false, MNISTApplication.seed)

  val model = Serialise.read(MNISTApplication.modelPath)

  val app = new MnistModelApplication(model)
  while(true) {
    app.doTraining(MNISTApplication.modelPath, mnistTrain, 5, 1)
    println(app.evaluate(testData = mnistTest, 10))
  }


}