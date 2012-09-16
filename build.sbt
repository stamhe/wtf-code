name := "wtfcode"

version := "1.0"

scalaVersion := "2.9.1"

seq(webSettings: _*)

scanDirectories in Compile := Nil

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  val liftVersion = "2.5-SNAPSHOT"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile",
    "net.liftmodules" %% "widgets" % (liftVersion + "-1.1-SNAPSHOT") % "compile")
}

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
  "com.h2database" % "h2" % "1.2.138",
  "ch.qos.logback" % "logback-classic" % "1.0.3" % "compile->default",
  "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7",
  "org.specs2" %% "specs2" % "1.11" % "test",
  "junit" % "junit" % "4.8" % "test->default"
)
