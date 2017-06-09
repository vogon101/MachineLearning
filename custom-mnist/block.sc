import com.vogonjeltz.trading.app.Utils

val text = Utils.readText("C:\\Users\\fredd\\Google Drive\\programming\\IdeaProjects\\TradingNetworks\\custom-mnist/1.csv").split(",").grouped(28).map(_.mkString(",")).mkString("\n")