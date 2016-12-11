name := "ZI-tat"

version := "0.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala, ElasticBeanstalkPlugin, BuildInfoPlugin)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += ws

libraryDependencies += "jp.co.bizreach" % "aws-dynamodb-scala_2.11" % "0.0.4"

// Docker/Elastic Beanstalk
maintainer in Docker := "Jeroen"
dockerExposedPorts := Seq(9000)
dockerBaseImage := "java:latest"

// BuildInfoPlugin
buildInfoPackage := "build"