package com.vogonjeltz.machineInt.app

import javax.imageio.IIOException

import com.vogonjeltz.machineInt.app.MNISTApplication.seed
import com.vogonjeltz.machineInt.app.MNISTApplication_MoreTrain.batchSize
import com.vogonjeltz.machineInt.lib.Serialise
import com.vogonjeltz.machineInt.lib.dl4jModels.mnist.{MnistModelApplication, MnistModelDef}
import org.deeplearning4j.api.storage.StatsStorage
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.ui.api.UIServer
import org.deeplearning4j.ui.stats.StatsListener
import org.deeplearning4j.ui.storage.InMemoryStatsStorage

/**
  * Created by Freddie on 09/06/2017.
  */
object MNISTApplication {

  //TODO: Convolutional neural networks
  //http://brooksandrew.github.io/simpleblog/articles/convolutional-neural-network-training-with-dl4j/
  //https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/convolution/LenetMnistExample.java
  //http://progur.com/2017/03/how-to-create-convolutional-neural-networks-java-dl4j.html
  //http://semantive.com/deep-learning-examples/


  //FIXME: turn off dropout for eval?
  val modelPath = "model/mnist/v3_dropout.zip"
  val seed = 100

  def main(args: Array[String]): Unit = {

    val model = Serialise.read(modelPath)

    val app = new MnistModelApplication(model)
    while(true){
      try{
        val o = app.use("custom-mnist\\images\\" + readLine(">") + ".bmp", true)
        println(s"Prediction = ${o._1}, Confidence = ${o._2(o._1)}")
      } catch {
        case e: IIOException =>
          println("Could not open file")
          println(e.getMessage)
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
  app.doTraining(MNISTApplication.modelPath, mnistTrain, 1, 1)
  println(app.evaluate(testData = mnistTest, 10))

}

object MNISTApplication_MoreTrain extends App {

  val batchSize = 200

  val mnistTrain = new MnistDataSetIterator(batchSize, true, MNISTApplication.seed)
  val mnistTest = new MnistDataSetIterator(batchSize, false, MNISTApplication.seed)

  val model = Serialise.read(MNISTApplication.modelPath)

  val app = new MnistModelApplication(model)
  while(true) {
    app.doTraining(MNISTApplication.modelPath, mnistTrain, 3, 1)
    println(app.evaluate(testData = mnistTest, 10))
  }


}

object MNISTApplication_Evaluate extends App {

  val batchSize = 200
  val mnistTest = new MnistDataSetIterator(batchSize, false, 100)
  val model = Serialise.read(MNISTApplication.modelPath)
  val app = new MnistModelApplication(model)
  println(app.evaluate(mnistTest, 10))

}

object  MNISTApplication_UI extends App {
  val batchSize = 200
  val mnistTest = new MnistDataSetIterator(batchSize, false, 100)

  val mnistTrain = new MnistDataSetIterator(batchSize, true, MNISTApplication.seed)
  val model = Serialise.read(MNISTApplication.modelPath)

  //Initialize the user interface backend
  val uiServer = UIServer.getInstance()

  //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
  val statsStorage = new InMemoryStatsStorage()        //Alternative: new FileStatsStorage(File), for saving and loading later

  //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
  uiServer.attach(statsStorage)

  //Then add the StatsListener to collect this information from the network, as it trains
  model.setListeners(new StatsListener(statsStorage))

  val app = new MnistModelApplication(model)
  println(app.evaluate(mnistTest, 10))

  while(true) {
    app.doTraining(MNISTApplication.modelPath, mnistTrain, 3, 1)
    println(app.evaluate(testData = mnistTest, 10))
  }


}