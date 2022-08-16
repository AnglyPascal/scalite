package com.anglypascal.scalite.data

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Num
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value

object DataExtensions:

  extension (data: Obj)
    def getOrElse(key: String)(default: String): String =
      if data.obj.contains(key) then
        data.obj(key) match
          case s: Str => s.str
          case _      => default
      else default

    def getOrElse(key: String)(default: Boolean): Boolean =
      if data.obj.contains(key) then
        data.obj(key) match
          case b: Bool => b.bool
          case _       => default
      else default

  extension (data: Value)
    def hardCopy: Value =
      data match
        case s: Str  => Str(s.str)
        case n: Num  => Num(n.num)
        case b: Bool => Bool(b.bool)
        case a: Arr  => Arr(a.arr.map(_.hardCopy))
        case o: Obj  => Obj(o.obj.map((k, v) => (k, v.hardCopy)))
        case other   => other

  extension (data: DObj)
    def getOrElse(key: String)(default: String): String =
      if data.contains(key) then
        data(key) match
          case s: DStr => s.str
          case _       => default
      else default

    def getOrElse(key: String)(default: Boolean): Boolean =
      if data.contains(key) then
        data(key) match
          case b: DBool => b.bool
          case _        => default
      else default
