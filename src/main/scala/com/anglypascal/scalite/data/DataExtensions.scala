package com.anglypascal.scalite.data

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Num
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value

import scala.collection.mutable.Map

object DataExtensions:

  extension (data: Obj)
    final def getOrElse(key: String)(default: String): String =
      if data.obj.contains(key) then
        data.obj(key) match
          case s: Str => s.str
          case _      => default
      else default

    final def getOrElse(key: String)(default: Boolean): Boolean =
      if data.obj.contains(key) then
        data.obj(key) match
          case b: Bool => b.bool
          case _       => default
      else default

    final def extractOrElse(key: String)(default: String): String =
      if data.obj.contains(key) then
        data.obj.remove(key) match
          case Some(s) =>
            s match
              case s: Str => s.str
              case _      => default
          case _ => default
      else default

    final def extractOrElse(key: String)(default: Boolean): Boolean =
      if data.obj.contains(key) then
        data.obj.remove(key) match
          case Some(s) =>
            s match
              case s: Bool => s.bool
              case _       => default
          case _ => default
      else default

    final def extractOrElse(
        key: String
    )(default: Map[String, Value]): Map[String, Value] =
      if data.obj.contains(key) then
        data.obj.remove(key) match
          case Some(s) =>
            s match
              case s: Obj => s.obj
              case _      => default
          case _ => default
      else default

@main
def dataExtensionsTest =
  import DataExtensions.*

  val d = DNum(5)
  println(d.toString)
