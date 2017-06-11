import com.vogonjeltz.machineInt.lib.Utils

val text = Utils.readText("1.csv").split(",").grouped(28).mkString("\n")