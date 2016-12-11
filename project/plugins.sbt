
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.10")

resolvers += Resolver.url("bintray-kipsigman-sbt-plugins", url("http://dl.bintray.com/kipsigman/sbt-plugins"))(Resolver.ivyStylePatterns)

addSbtPlugin("kipsigman" % "sbt-elastic-beanstalk" % "0.1.4")

// To get build info in the app
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")
