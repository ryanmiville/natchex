ThisBuild / tlBaseVersion := "0.1"

ThisBuild / licenses := Seq(License.MIT)
ThisBuild / tlUntaggedAreSnapshots := false
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / crossScalaVersions := Seq("3.1.2", "2.13.8")
ThisBuild / scalaVersion := "2.13.8"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = tlCrossRootProject.aggregate(core, examples)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.tpolecat" %%% "natchez-core" % "0.1.6",
      "co.fs2" %%% "fs2-core" % "3.2.7"
    )
  )

lazy val examples = project
  .in(file("examples"))
  .settings(
    name := "examples",
    libraryDependencies ++= Seq(
      "org.tpolecat" %%% "natchez-log" % "0.1.6",
      "org.typelevel" %%% "log4cats-core" % "2.3.1",
    )
  )
  .dependsOn(core.jvm)
