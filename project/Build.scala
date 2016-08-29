import sbt._

object MyBuild extends Build {

  lazy val root = Project("root", file(".")) // dependsOn miseImplementation
//  lazy val miseImplementation =
//    RootProject(uri("git://github.com/nicoring/MISE.git"))

}