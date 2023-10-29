lazy val root = project
  .in(file("."))
  .settings(
    name := "Glassicon - The glassy Identicon",
    version := "0.0.1",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.14",
      "com.typesafe.akka" %% "akka-stream-typed" % "2.6.14",
      "com.typesafe.akka" %% "akka-http" % "10.2.4",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4"
    )
  )
