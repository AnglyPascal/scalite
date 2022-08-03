package com.anglypascal.scalite.utils

import scala.io.Source
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Value

def readFile(filename: String): Source = Source.fromFile(filename)

extension (data: Obj)
  def getOrElse[A <: Value](key: String)(default: A): A =
    if data.obj.contains(key) then
      data.obj(key) match 
        case a: A => a
        case _ => default
    else default
