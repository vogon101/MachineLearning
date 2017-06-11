name := "WebMachineLearning"

version := "1.0"

lazy val `webmachinelearning` = (project in file(".")).enablePlugins(PlayScala, LauncherJarPlugin)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

unmanagedBase := baseDirectory.value / "lib"

libraryDependencies  ++= Seq(
  "org.scalanlp" %% "breeze" % "0.13",
  "org.scalanlp" %% "breeze-natives" % "0.13",
  "org.scalanlp" %% "breeze-viz" % "0.13",
  "net.liftweb" % "lift-json_2.11" % "3.0.1"
)

// https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-core
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "0.8.0"

/*libraryDependencies ++= Seq(
  "org.nd4j" % "nd4j-native" % "0.5.0" classifier "windows-x86_64",
  "org.nd4j" % "nd4j-native" % "0.5.0"
)*/
// https://mvnrepository.com/artifact/org.nd4j/nd4j-native-platform
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "0.8.0"

libraryDependencies ++= Seq(
  "com.twelvemonkeys.imageio" % "imageio" % "3.1.2",
  "com.twelvemonkeys.imageio" % "imageio-core" % "3.1.2",
  "com.twelvemonkeys.common" % "common-lang" % "3.1.2"
)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  