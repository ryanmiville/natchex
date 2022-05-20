ThisBuild / tlBaseVersion := "0.1"

ThisBuild / licenses := Seq(License.MIT)
ThisBuild / tlUntaggedAreSnapshots := false
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / crossScalaVersions := Seq("3.1.2", "2.13.8")
ThisBuild / scalaVersion := "2.13.8"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "core",
    libraryDependencies += "org.tpolecat" %%% "natchez-core" % "0.1.6"
  )
