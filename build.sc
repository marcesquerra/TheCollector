import mill._, scalalib._, publish._

import TheCollectorModule._

object main extends Cross[TheCollectorModule](jvmScalaVersions: _*)

class TheCollectorModule(val crossScalaVersion: String)
    extends CrossScalaModule
    with PublishModule {
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

  val jsScalaVersions = Seq(scala11)
  val nativeScalaVersions = Seq(scala11)
  val jvmScalaVersions = Seq(scala10, scala11, scala12, scala13)
  // val jvmScalaVersions = Seq(scala11)

  val nameLiteral = "TheCollector"
  //TRAVIS_BUILD_NUMBER
  lazy val buildNumber: Int =
    scala.util.Properties.envOrElse("TRAVIS_BUILD_NUMBER", "0").toInt
}
