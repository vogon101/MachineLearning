name := "Trading"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "0.13",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "0.13",

  // https://mvnrepository.com/artifact/gov.nist.math/jama

  "net.liftweb" % "lift-json_2.11" % "3.0.1"

)

// https://mvnrepository.com/artifact/org.apache.commons/commons-math3
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.0"

fork in run := true

outputStrategy in run := Some(StdoutOutput)

connectInput in run := true

javaOptions in run ++= Seq(
  "-Xms256M", "-Xmx2G", "-XX:MaxPermSize=1024M", "-XX:+UseConcMarkSweepGC")


mainClass in (Compile, run) := Some("com.vogonjeltz.trading.app.KalmanTest")