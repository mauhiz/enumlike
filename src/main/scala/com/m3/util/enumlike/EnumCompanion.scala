package com.m3.util.enumlike
import play.api.libs.json._

trait EnumCompanion[A <: EnumLike] {

  def values: IndexedSeq[A]

  def valueOf(value: A#ValueType): Option[A] = values.find(_.value == value)

  implicit def jsonFormat(implicit f: Format[A#ValueType]): Format[A] = new Format[A] {
    override def writes(o: A): JsValue = f.writes(o.value)
    override def reads(json: JsValue): JsResult[A] = f.reads(json).flatMap { value =>
      valueOf(value).fold[JsResult[A]](JsError(s"Could not convert $value"))(JsSuccess(_, JsPath()))
    }
  }

  implicit def companion: EnumCompanion[A] = this

  implicit def order: Ordering[A] = Ordering.by(values.indexOf)

}