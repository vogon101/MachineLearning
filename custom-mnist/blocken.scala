import com.vogonjeltz.trading.app.Utils

val text = Utils.readText("1.csv").split(",").grouped(28).mkString("\n")