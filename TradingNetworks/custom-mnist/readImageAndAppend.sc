import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import com.vogonjeltz.machineInt.lib.Utils

val path = "C:\\Users\\Freddie\\Google Drive\\programming\\IdeaProjects\\TradingNetworks\\custom-mnist"

val image = ImageIO.read(new File(path + "\\three.bmp"))

val imageArray = Array.ofDim[Double](28,28)

val newImage = new BufferedImage(28,28,image.getType())
val g = newImage.createGraphics()
g.rotate(Math.toRadians(90), 14,14)



g.drawImage(image, 0, 28, 28 , -28, null)

var finalText = new StringBuilder()

for (i <- Range(0, 28)){
  for (j <- Range(0, 28)) {
    val colour = newImage.getRGB(i,j)
    val r = (255 - ((colour & 0xff0000) >> 16))/255d
    if (r > 0.1) print("X,") else print("-,")
    val text = if (r > 0.01) f"$r%1.2f," else "0.00,"
    finalText.append(text)
  }
  println()
}

val cat = "3"
finalText.append(cat)
val newText = Utils.readText(path + "\\1.csv") + "\n" + finalText.toString
Utils.writeText(newText, path + "\\1.csv")
