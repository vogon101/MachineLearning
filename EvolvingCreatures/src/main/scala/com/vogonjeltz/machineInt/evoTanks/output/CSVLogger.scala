package com.vogonjeltz.machineInt.evoTanks.output

import java.io.FileWriter

/**
  * CSVLogger
  *
  * Created by fredd
  */
class CSVLogger(val path: String) {

  val fw = new FileWriter(path, true)

  def append(items: Any*): Unit = {
    //print(items.map(_.toString).mkString(",") + "\n")
    fw.write(items.mkString(",") + "\n")
    fw.flush()
  }

  def close(): Unit = fw.close()

}
