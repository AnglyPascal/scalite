package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Obj
import scala.collection.mutable.ArrayBuffer
import com.rallyhealth.weejson.v1.Arr
import com.anglypascal.scalite.data.DataExtensions.getOrElse

final case class Scope(path: String, rType: String = null, conf: Obj = Obj()):
  /** check if path is inside this scope */
  def contains(file: String, rT: String) =
    file.startsWith(path) && rT == rType

object ScopedDefaults:
  private val scopes = ArrayBuffer[Scope]()

  def apply(base: String, defs: Arr): Unit =
    for v <- defs.arr if v.obj.contains("scope") && v.obj.contains("values") do
      v match
        case v: Obj =>
          val o = v("values") match
            case x: Obj => x
            case _      => Obj()
          val p = v("scope").obj.getOrElse("path")("")
          val r = v("scope").obj.getOrElse("type")("")
          scopes += Scope(p, r, o)
        case _ => ()

  def getDefaults(file: String, rT: String) =
    val obj = Obj()
    for scope <- scopes if scope.contains(file, rT) do
      obj.obj ++= scope.conf.obj
    obj
