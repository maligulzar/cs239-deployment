name := "capstone"

version := "0.1"

scalaVersion := "2.11.11"

val sparkVersion = "2.1.0"

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
//    "com.holdenkarau" %% "spark-testing-base" % "2.1.0_0.7.4" % "test",
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
