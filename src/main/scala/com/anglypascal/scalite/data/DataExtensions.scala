package com.anglypascal.scalite.data

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Num
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value

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

  extension (data: DObj)
    final def getOrElse(key: String)(default: String): String =
      if data.contains(key) then
        data(key) match
          case s: DStr => s.str
          case _       => default
      else default

    final def getOrElse(key: String)(default: Boolean): Boolean =
      if data.contains(key) then
        data(key) match
          case b: DBool => b.bool
          case _        => default
      else default

    final def getOrElse(key: String)(default: List[Data]): List[Data] =
      if data.contains(key) then
        data(key) match
          case b: DArr => b._arr
          case _       => default
      else default

  extension (data: Data)

    final def getStr: Option[String] =
      data match
        case data: DStr => Some(data.str)
        case _          => None

    final def getNum: Option[BigDecimal] =
      data match
        case data: DNum => Some(data.num)
        case _          => None

    final def getBool: Option[Boolean] =
      data match
        case data: DBool => Some(data.bool)
        case _           => None

    final def getArr: Option[List[Data]] =
      data match
        case data: DArr => Some(data._arr)
        case _          => None

    final def getObj: Option[Map[String, Data]] =
      data match
        case data: DObj => Some(data._obj)
        case _          => None
