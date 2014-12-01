package com.m3.util.enumlike

import com.m3.util.enumlike.Beatle._
import org.scalatest.{ Matchers, FlatSpec }

class CustomBinderBuildersSpec extends FlatSpec with Matchers with CustomBinderBuilders {

  behavior of "#enumPathBindable"

  val beatleEnumPathBindable = enumPathBindable(BeatleEnum)

  it should "bind a known String" in {
    beatleEnumPathBindable.bind("name", "Ringo") should be(Right(BeatleEnum.Ringo))
  }

  it should "fail to bind an unknown String" in {
    beatleEnumPathBindable.bind("name", "Chris") should be('left)
  }

  it should "unbind" in {
    beatleEnumPathBindable.unbind("name", BeatleEnum.Ringo) should be("Ringo")
  }

  behavior of "#enumLikePathBindable"

  val beatlePathBindable = enumLikePathBindable[Beatle]

  it should "bind a known String" in {
    beatlePathBindable.bind("name", "Ringo") should be(Right(Ringo))
  }

  it should "fail to bind an unknown String" in {
    beatlePathBindable.bind("name", "Chris") should be('left)
  }

  it should "unbind" in {
    beatlePathBindable.unbind("name", Ringo) should be("Ringo")
  }

  behavior of "#enumQueryStringBindable"

  val beatleEnumQueryStringBindable = enumQueryStringBindable(BeatleEnum)

  it should "bind a known String" in {
    beatleEnumQueryStringBindable.bind("name", Map("foo" -> Seq("bar"), "name" -> Seq("Ringo"))) should be(Some(Right(BeatleEnum.Ringo)))
  }

  it should "fail to bind an unknown String" in {
    beatleEnumQueryStringBindable.bind("name", Map("foo" -> Seq("bar"), "name" -> Seq("Chris"))).get should be('left)
  }

  it should "not bind if param is not present" in {
    beatleEnumQueryStringBindable.bind("name", Map("foo" -> Seq("bar"))) should be(None)
  }

  it should "unbind" in {
    beatleEnumQueryStringBindable.unbind("name", BeatleEnum.Ringo) should be("name=Ringo")
  }

  behavior of "#enumLikeQueryStringBindable"

  val beatleQueryStringBindable = enumLikeQueryStringBindable[Beatle]

  it should "bind a known String" in {
    beatleQueryStringBindable.bind("name", Map("foo" -> Seq("bar"), "name" -> Seq("Ringo"))) should be(Some(Right(Ringo)))
  }

  it should "fail to bind an unknown String" in {
    beatleQueryStringBindable.bind("name", Map("foo" -> Seq("bar"), "name" -> Seq("Chris"))).get should be('left)
  }

  it should "not bind if param is not present" in {
    beatleQueryStringBindable.bind("name", Map("foo" -> Seq("bar"))) should be(None)
  }

  it should "unbind" in {
    beatleQueryStringBindable.unbind("name", Ringo) should be("name=Ringo")
  }

}
