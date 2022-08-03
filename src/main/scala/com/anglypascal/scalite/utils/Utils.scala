package com.anglypascal.scalite.utils

import scala.io.Source
import com.rallyhealth.weejson.v1.{Value, Obj, Str, Bool}
import com.rallyhealth.weejson.v1.Value

def readFile(filename: String): Source = Source.fromFile(filename)

extension (data: Obj)
  def getOrElse(key: String)(default: String): String =
    if data.obj.contains(key) then
      data.obj(key) match 
        case s: Str => s.str
        case _ => default
    else default

  def getOrElse(key: String)(default: Boolean): Boolean =
    if data.obj.contains(key) then
      data.obj(key) match 
        case b: Bool => b.bool
        case _ => default
    else default
