name := "natchex"

ThisBuild / tlBaseVersion := "0.1"
ThisBuild / tlUntaggedAreSnapshots := false
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / crossScalaVersions := Seq("3.1.2", "2.13.8")

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "core",
    libraryDependencies += "org.tpolecat" %%% "natchez-core" % "0.1.6"
  )