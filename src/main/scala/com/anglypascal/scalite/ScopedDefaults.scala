package com.anglypascal.scalite

import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap
import com.typesafe.scalalogging.Logger

object ScopedDefaults extends Configurable:

  private val scopes = LinkedHashMap[(String, String), MObj]()

  val sectionName: String = "defaults"

  def apply(conf: MObj, globals: IObj): Unit =
    val base = globals.getOrElse("base")(Defaults.Directories.base)
    for (k, v) <- conf do
      v match
        case v: MObj if v.contains("values") && v.contains("scope") =>
          val o = v.getOrElse("values")(MObj())

          val s = v.getOrElse("scope")(MObj())
          val p = s.getOrElse("path")("")
          val r = s.getOrElse("type")("")

          scopes += (base + p, r) -> o
        case _ => ()

  def getDefaults(file: String, rT: String) =
    val obj = MObj()
    for (k, v) <- scopes do
      if file.contains(k._1) && (rT == k._2 || rT == "") then obj update v
    obj
