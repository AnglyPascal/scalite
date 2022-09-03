package com.anglypascal.scalite

import com.anglypascal.scalite.data.mutable.DObj
import com.anglypascal.scalite.data.mutable.DArr
import scala.collection.mutable.ArrayBuffer
import com.anglypascal.scalite.data.DataExtensions.getOrElse
import scala.collection.mutable.LinkedHashMap

object ScopedDefaults:
  private val scopes = LinkedHashMap[(String, String), DObj]()

  def apply(base: String, defs: DArr): Unit =
    for v <- defs do
      v match
        case v: DObj if v.contains("values") && v.contains("scope") =>
          val o = v.getOrElse("values")(DObj())

          val s = v.getOrElse("scope")(DObj())
          val p = s.getOrElse("path")("")
          val r = s.getOrElse("type")("")

          scopes += (p, r) -> o
        case _ => ()

  def getDefaults(file: String, rT: String) =
    val obj = DObj()
    if scopes.contains(file, rT) then obj update scopes(file, rT)
    obj
