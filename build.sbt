import _root_.sbt.Keys._
import _root_.sbt.Project
import _root_.sbtrelease.ReleaseStateTransformations
import _root_.sbtrelease.ReleaseStateTransformations._
import sbtrelease._
import ReleaseStateTransformations._
import ReleaseKeys._
import xerial.sbt.Sonatype.SonatypeKeys
import com.typesafe.sbt.SbtGit.{GitKeys => git}
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scala10 = "2.10.7"
val scala11 = "2.11.12"
val scala12 = "2.12.8"
val scala13 = "2.13.0"

val jsScalaVersions = Seq(scala11)
val nativeScalaVersions = Seq(scala11)
val jvmScalaVersions = Seq(scala10, scala11, scala12, scala13)
// val jvmScalaVersions = Seq(scala11)

val nameLiteral = "TheCollector"
val sharedSettings: sbt.Def.SettingsDefinition =
  site.settings          ++
  ghpages.settings       ++
  site.includeScaladoc() ++
Seq(

organization := s"com.bryghts.${nameLiteral.toLowerCase}",

git.gitRemoteRepo := s"git@github.com:marcesquerra/$nameLiteral.git",

name := nameLiteral,

scalaVersion := scala11,

publishMavenStyle := true,

sonatypeProfileName  := "com.bryghts",

libraryDependencies ++= Seq( ),

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
},

publishArtifact in Test := false,

pomExtra := (
    <url>http://www.brights.com</url>
        <licenses>
            <license>
                <name>mit</name>
            </license>
        </licenses>
        <scm>
            <url>git@github.com:marcesquerra/{nameLiteral}.git</url>
            <connection>scm:git:git@github.com:marcesquerra/{nameLiteral}.git</connection>
        </scm>
        <developers>
            <developer>
                <name>Marc Esquerrà i Bayo</name>
                <email>esquerra@bryghts.com</email>
            </developer>
        </developers>
    ),


releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,                    // : ReleaseStep
    inquireVersions,                              // : ReleaseStep
    runClean,                                     // : ReleaseStep
    runTest,                                      // : ReleaseStep
    setReleaseVersion,                            // : ReleaseStep
    commitReleaseVersion,                         // : ReleaseStep, performs the initial git checks
    tagRelease,                                   // : ReleaseStep
    ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
    setNextVersion,                               // : ReleaseStep
    commitNextVersion,                            // : ReleaseStep
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges                                   // : ReleaseStep, also checks that an upstream branch is properly configured
)


)


val root =
    // select supported platforms
    crossProject(JSPlatform, JVMPlatform, NativePlatform)
      .withoutSuffixFor(JVMPlatform)
      .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
      .in(file("."))
      .jsSettings(sharedSettings ++ Seq( crossScalaVersions := jsScalaVersions ))
      .jvmSettings(sharedSettings ++ Seq( crossScalaVersions := jvmScalaVersions ))
      .nativeSettings(sharedSettings ++ Seq( crossScalaVersions := nativeScalaVersions ))
      .settings(Seq(
        crossScalaVersions := Seq()
      ))

// Optional in sbt 1.x (mandatory in sbt 0.13.x)
lazy val rootJS     = root.js
lazy val rootJVM    = root.jvm
lazy val rootNative = root.native
