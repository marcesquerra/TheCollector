import mill._, scalalib._, publish._, mill.scalajslib._, mill.scalanativelib._,
mill.scalanativelib.api.ReleaseMode

import TheCollectorModule._

object main extends Module {

  object jvmMain extends Cross[TheCollectorJvmModule](jvmScalaVersions: _*)
  object jsMain extends Cross[TheCollectorJsModule](jsScalaVersions: _*)
  object nativeMain
      extends Cross[TheCollectorNativeModule](nativeScalaVersions: _*)

}

class TheCollectorJvmModule(
    val crossScalaVersion: String
) extends TheCollectorModule { self =>

  def platformSegment = "jvm"

  // trait Tests extends super.Tests with CommonTestModule {
  //   def platformSegment = self.platformSegment
  // }
}

class TheCollectorJsModule(
    val crossScalaVersion: String
) extends TheCollectorModule
    with ScalaJSModule { self =>

  def platformSegment = "js"
  def scalaJSVersion = "0.6.28"

  // trait Tests extends super.Tests with CommonTestModule {
  //   def platformSegment = self.platformSegment
  // }
}

class TheCollectorNativeModule(
    val crossScalaVersion: String
) extends TheCollectorModule
    with ScalaNativeModule { self =>

  def platformSegment = "js"
  def scalaNativeVersion = "0.3.9"
  def releaseMode = ReleaseMode.Release

  // trait Tests extends super.Tests with CommonTestModule {
  //   def platformSegment = self.platformSegment
  // }
}

trait TheCollectorModule extends CrossScalaModule with PublishModule {

  def platformSegment: String
  def millSourcePath = super.millSourcePath / os.up

  def sources = T.sources(
    millSourcePath / "src",
    millSourcePath / s"src-$platformSegment"
  )

  def artifactName = s"com.bryghts.${nameLiteral.toLowerCase}"
  def publishVersion = s"0.0.${buildNumber}"

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = s"com.bryghts.${nameLiteral.toLowerCase}",
    url = s"https://github.com/marcesquerra/$nameLiteral",
    licenses = Seq(License.Unlicense),
    scm = SCM(
      s"git://github.com/marcesquerra/${nameLiteral}.git",
      s"scm:git://github.com/marcesquerra/${nameLiteral}.git"
    ),
    developers = Seq(
      Developer(
        "marcesquerra",
        "Marc Esquerrà i Bayo",
        "https://github.com/marcesquerra"
      )
    )
  )

}

object TheCollectorModule {
  val scala10 = "2.10.7"
  val scala11 = "2.11.12"
  val scala12 = "2.12.8"
  val scala13 = "2.13.0"

  val jsScalaVersions = Seq(scala10, scala11, scala12, scala13)
  val nativeScalaVersions = Seq(scala11)
  val jvmScalaVersions = Seq(scala10, scala11, scala12, scala13)

  val nameLiteral = "TheCollector"
  //TRAVIS_BUILD_NUMBER
  lazy val buildNumber: Int =
    scala.util.Properties.envOrElse("TRAVIS_BUILD_NUMBER", "0").toInt
}
