name := "EvoTanks"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.9.0" classifier "natives-windows" classifier "natives-linux" classifier "natives-osx",
  "slick-util" % "slick-util" % "1.0.0" from "http://slick.ninjacave.com/slick-util.jar",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.9.0"
)

// Native libraries extraction - LWJGL has some native libraries provided as JAR files that I have to extract
compile in Compile <<= (compile in Compile).dependsOn(Def.task {
  val r = "^(\\w+).*".r
  val r(os) = System.getProperty( "os.name" )

  val jars = ( update in Compile ).value
    .select( configurationFilter( "compile" ) )
    .filter( _.name.contains( os.toLowerCase ) )

  jars foreach { jar =>
    println( s"[info] Processing '${jar.getName}' and saving to '${unmanagedBase.value}'" )
    IO.unzip(  jar, unmanagedBase.value )
  }

  Seq.empty[File]
})



mainClass in (Compile, run) := Some("com.vogonjeltz.machineInt.evoTanks.app.EvoTanksApp")
