import aether.Aether._
import com.typesafe.sbt.SbtGit._
import com.typesafe.sbt.SbtScalariform._
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin._

import scala.language.postfixOps

object EnumLikeBuild extends Build {
  val theScalaVersion = "2.11.6"
  val thePlayVersion = "2.3.7"

  val m3Resolver = "M3 internal Artifactory" at "http://maven:8081/artifactory/repo"

  val commonSettings = Seq(
    organization := "com.m3",
    scalaVersion := theScalaVersion,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint"),
    updateOptions := updateOptions.value
      .withCircularDependencyLevel(CircularDependencyLevel.Error)
      .withCachedResolution(true)
  ) ++ scalariformSettings ++ publishSettings ++ releaseSettings

  val excludes = Seq(
    ExclusionRule(name = "slf4j-log4j12"),
    ExclusionRule(name = "slf4j-jdk14"),
    ExclusionRule(name = "slf4j-jcl"),
    ExclusionRule(name = "slf4j-nop"),
    ExclusionRule(name = "slf4j-simple")
  )

  lazy val core = Project(id = "enumlike", base = file("core"))
    .settings(commonSettings:_*)
    .settings(
      name := "enumlike",
      libraryDependencies := Seq(
        "com.beachape"  %% "enumeratum-macros" % "0.0.5",
        "org.scalatest" %% "scalatest"         % "2.2.2"   % "test"
      ).map(_.excludeAll(excludes:_*))
    )

  lazy val play = Project(id = "enumlike-play", base = file("play"))
    .settings(commonSettings:_*)
    .settings(
      name := "enumlike-play",
      libraryDependencies := Seq(
        "com.typesafe.play" %% "play-ws"   % thePlayVersion, // this library depends on play-json and Play MVC
        "com.typesafe.play" %% "play-test" % thePlayVersion % "test",
        "org.scalatestplus" %% "play"      % "1.2.0"        % "test",
        "org.scalatest"     %% "scalatest" % "2.2.2"        % "test"
      ).map(_.excludeAll(excludes:_*))
    ).dependsOn(core % "compile->compile;test->test")

  lazy val scalikejdbc = Project(id = "enumlike-scalikejdbc", base = file("scalikejdbc"))
    .settings(commonSettings:_*)
    .settings(
      name := "enumlike-scalikejdbc",
      libraryDependencies := Seq(
        "org.scalikejdbc" %% "scalikejdbc" % "2.2.4",
        "org.scalatest"   %% "scalatest"   % "2.2.2" % "test"
      ).map(_.excludeAll(excludes:_*))
    ).dependsOn(core % "compile->compile;test->test")

  lazy val publishSettings = aetherPublishSettings :+
    (publishTo <<= version { (v: String) =>
      val base = "http://maven:8081/artifactory/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at base + "libs-snapshots")
      else Some("releases" at base + "libs-releases")
    })

}
