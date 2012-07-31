name := "wtfcode"

version := "1.0"

scalaVersion := "2.9.1"

seq(webSettings: _*)

scanDirectories in Compile := Nil

resolvers ++= Seq(
  "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)

libraryDependencies ++= {
  val liftVersion = "2.4"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-textile" % liftVersion % "compile->default")
}

libraryDependencies ++= Seq(
  "org.mortbay.jetty" % "jetty" % "6.1.25" % "container",
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test",
  "junit" % "junit" % "4.8" % "test->default",
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "com.h2database" % "h2" % "1.2.138",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"
)