package com.vogonjeltz.trading.app

import java.io.File

import com.vogonjeltz.trading.lib.Serialise
import org.datavec.api.records.reader.impl.csv.CSVRecordReader
import org.datavec.api.split.FileSplit
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.conf.{NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction

import scala.collection.JavaConversions.asScalaIterator

/**
  * DL4JTest
  *
  * Created by fredd
  */
object DL4JTest extends App {

  val imageSize = 28
  val rngSeed = 100
  val batchSize = 200

  val mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed)
  val mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed)

  println(mnistTest.next().get(0))

  val command = readLine(">>>")
  if (command == "train") {

    val conf = new NeuralNetConfiguration.Builder()
      .seed(rngSeed)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .iterations(1)
      .learningRate(0.006)
      .updater(Updater.NESTEROVS)
      .momentum(0.9)
      .regularization(true).l2(1e-4)
      .list()
      .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
        .nIn(imageSize * imageSize)
        .nOut(1000)
        .activation(Activation.RELU)
        .weightInit(WeightInit.XAVIER)
        .build())
      .layer(1, new DenseLayer.Builder()
        .nIn(1000)
        .nOut(300)
        .build())
      .layer(2, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
        .nIn(300)
        .nOut(10)
        .activation(Activation.SOFTMAX)
        .weightInit(WeightInit.XAVIER)
        .build())
      .pretrain(false).backprop(true) //use backpropagation to adjust weights
      .build()

    val model = new MultiLayerNetwork(conf)
    model.init()
    model.setListeners(new ScoreIterationListener(1))

    for (i <- Range(0, 15)) {
      for (j <- Range(0, 5)) {
        println(s"Epoch ${i * 5 + j}")
        model.fit(mnistTrain)
      }

      val eval = new Evaluation(10)
      model.doEvaluation(mnistTest, eval)
      println(eval.stats)
      mnistTest.reset()

      Serialise.save("model/mnist_trained.zip", model)

    }

    Serialise.save("model/mnist_trained.zip", model)
  } else if (command == "test") {
    val model = Serialise.read("model/mnist_trained.zip")
    val eval = new Evaluation(10)
    model.doEvaluation(mnistTest, eval)
    println(eval.stats)
  } else if (command == "moreTrain") {
    val model = Serialise.read("model/mnist_trained.zip")
    model.setListeners(new ScoreIterationListener(1))
    for (i <- Range(0, 15)) {
      for (j <- Range(0, 5)) {
        println(s"Epoch ${i * 5 + j}")
        model.fit(mnistTrain)
      }

      val eval = new Evaluation(10)
      model.doEvaluation(mnistTest, eval)
      println(eval.stats)
      mnistTest.reset()

      Serialise.save("model/mnist_trained.zip", model)

    }
  } else if (command == "custom") {

    val numLinesToSkip = 0
    val delimiter = ","

    val labelIndex=28*28
    val numClasses = 10
    val maxNum = 9
    val batchSize = (maxNum + 1) * 5


    val recordReader = new CSVRecordReader(numLinesToSkip,delimiter)

    val folder = "custom-mnist/images/"

    val stringBuilder = new StringBuilder()

    for (i <- Range(0, maxNum + 1)) {
      for (j <- Range(0,5)) {
        val path = folder + s"$i-$j.bmp"
        val image = Utils.readBWBMP(path, 28)
        stringBuilder.append(image.map(X => if (X > 0.1) f"$X%1.2f" else "0.00").mkString(","))
        stringBuilder.append(s",$i\n")
      }
    }

    val tempPath = folder + "tmp.csv"
    Utils.writeText(stringBuilder.mkString, tempPath)

    recordReader.initialize(new FileSplit(new File(tempPath)))

    val iterator = new RecordReaderDataSetIterator(recordReader,batchSize,labelIndex,numClasses)
    println(iterator.next.get(0))
    iterator.reset()

    val model = Serialise.read("model/mnist_trained.zip")

    val eval = new Evaluation(numClasses)
    model.doEvaluation(iterator, eval)
    println(eval.stats)

  }

}
