package com.vogonjeltz.machineInt.app
/*
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.FilenameUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.datavec.api.io.labels.ParentPathLabelGenerator
import org.datavec.api.split.FileSplit
import org.datavec.image.loader.NativeImageLoader
import org.datavec.image.recordreader.ImageRecordReader
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io._
import java.util.Random
*/
/**
  * NotJustTheNumbersButTheLettersToo
  *
  * Created by fredd
  */
/*
object NotJustTheNumbersButTheLettersToo {
  /** Data URL for downloading */
  val DATA_URL = "http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz"
  /** Location to save and extract the training/testing data */
  val DATA_PATH: String = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_Mnist/")
  private val log = LoggerFactory.getLogger(NotJustTheNumbersButTheLettersToo.getClass)

  @throws[Exception]
  def main(args: Array[String]) {
    // image information
    // 28 * 28 grayscale
    // grayscale implies single channel
    val height = 28
    val width = 28
    val channels = 1
    val rngseed = 123
    val randNumGen = new Random(rngseed)
    val batchSize = 128
    val outputNum = 10
    val numEpochs = 15
    /*
            This class downloadData() downloads the data
            stores the data in java's tmpdir
            15MB download compressed
            It will take 158MB of space when uncompressed
            The data can be downloaded manually here
            http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz
             */ downloadData()
    // Define the File Paths
    val trainData = new File(DATA_PATH + "/mnist_png/training")
    val testData = new File(DATA_PATH + "/mnist_png/testing")
    // Define the FileSplit(PATH, ALLOWED FORMATS,random)
    val train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen)
    val test = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen)
    // Extract the parent path as the image label
    val labelMaker = new ParentPathLabelGenerator
    val recordReader = new ImageRecordReader(height, width, channels, labelMaker)
    // Initialize the record reader
    // add a listener, to extract the name
    recordReader.initialize(train)
    //recordReader.setListeners(new LogRecordListener());
    // DataSet Iterator
    val dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum)
    // Scale pixel values to 0-1
    val scaler = new ImagePreProcessingScaler(0, 1)
    scaler.fit(dataIter)
    dataIter.setPreProcessor(scaler)
    // Build Our Neural Network
    log.info("******LOAD TRAINED MODEL******")
    // Details
    // Where the saved model would be if
    // MnistImagePipelineSave has been run
    val locationToSave = new File("trained_mnist_model.zip")
    if (locationToSave.exists) System.out.println("\n######Saved Model Found######\n")
    else {
      System.out.println("\n\n#######File not found!#######")
      System.out.println("This example depends on running ")
      System.out.println("MnistImagePipelineExampleSave")
      System.out.println("Run that Example First")
      System.out.println("#############################\n\n")
      System.exit(0)
    }
    val model = ModelSerializer.restoreMultiLayerNetwork(locationToSave)
    model.getLabels
    //Test the Loaded Model with the test data
    recordReader.initialize(test)
    val testIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum)
    scaler.fit(testIter)
    testIter.setPreProcessor(scaler)
    // Create Eval object with 10 possible classes
    val eval = new Evaluation(outputNum)
    while (testIter.hasNext) {
      val next = testIter.next
      val output = model.output(next.getFeatures)
      eval.eval(next.getLabels, output)
    }
    log.info(eval.stats)
  }

  /*
Everything below here has nothing to do with your RecordReader,
or DataVec, or your Neural Network
The classes downloadData, getMnistPNG(),
and extractTarGz are for downloading and extracting the data
 */
  @throws[Exception]
  private def downloadData() {
    //Create directory if required
    val directory = new File(DATA_PATH)
    if (!directory.exists) directory.mkdir
    //Download file:
    val archizePath = DATA_PATH + "/mnist_png.tar.gz"
    val archiveFile = new File(archizePath)
    val extractedPath = DATA_PATH + "mnist_png"
    val extractedFile = new File(extractedPath)
    if (!archiveFile.exists) {
      System.out.println("Starting data download (15MB)...")
      getMnistPNG()
      //Extract tar.gz file to output directory
      extractTarGz(archizePath, DATA_PATH)
    }
    else {
      //Assume if archive (.tar.gz) exists, then data has already been extracted
      System.out.println("Data (.tar.gz file) already exists at " + archiveFile.getAbsolutePath)
      if (!extractedFile.exists) {
        //Extract tar.gz file to output directory
        extractTarGz(archizePath, DATA_PATH)
      }
      else System.out.println("Data (extracted) already exists at " + extractedFile.getAbsolutePath)
    }
  }

  private val BUFFER_SIZE = 4096

  @throws[IOException]
  private def extractTarGz(filePath: String, outputPath: String) {
    var fileCount = 0
    var dirCount = 0
    System.out.print("Extracting files")
    try{
      val tais = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(filePath))))
      try {
        var entry: TarArchiveEntry = null
        /** Read the tar entries using the getNextEntry method **/
        while ((entry = tais.getNextEntry.asInstanceOf[TarArchiveEntry]) != null) {
          //System.out.println("Extracting file: " + entry.getName());
          //Create directories as required
          if (entry.isDirectory) {
            new File(outputPath + entry.getName).mkdirs
            dirCount += 1
          }
          else {
            var count = 0
            val data = new Array[Byte](BUFFER_SIZE)
            val fos = new FileOutputStream(outputPath + entry.getName)
            val dest = new BufferedOutputStream(fos, BUFFER_SIZE)
            while ((count = tais.read(data, 0, BUFFER_SIZE)) != -1) dest.write(data, 0, count)
            dest.close()
            fileCount += 1
          }
          if (fileCount % 1000 == 0) System.out.print(".")
        }
      }
      finally if (tais != null) tais.close()
    }


    System.out.println("\n" + fileCount + " files and " + dirCount + " directories extracted to: " + outputPath)
  }

  @throws[IOException]
  def getMnistPNG() {
    val tmpDirStr = System.getProperty("java.io.tmpdir")
    val archizePath = DATA_PATH + "/mnist_png.tar.gz"
    if (tmpDirStr == null) throw new IOException("System property 'java.io.tmpdir' does specify a tmp dir")
    val url = "http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz"
    val f = new File(archizePath)
    val dir = new File(tmpDirStr)
    if (!f.exists) {
      val builder = HttpClientBuilder.create
      val client = builder.build
      try
        val response = client.execute(new HttpGet(url))
        try
          val entity = response.getEntity
          if (entity != null) try
            val outstream = new FileOutputStream(f)
            try
              entity.writeTo(outstream)
              outstream.flush()
              outstream.close()
            finally if (outstream != null) outstream.close()

          finally if (response != null) response.close()

          System.out.println("Data downloaded to " + f.getAbsolutePath)
    }
    else System.out.println("Using existing directory at " + f.getAbsolutePath)
  }

}
*/