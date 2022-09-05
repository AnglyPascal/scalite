package com.anglypascal.scalite

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap

/** Fetches scoped defaults from the "defaults" section of configurations
  */
object ScopedDefaults extends Configurable:

  private val scopes = LinkedHashMap[(String, String), MObj]()

  private var base: String = _

  val sectionName: String = "defaults"

  def apply(conf: MObj, globals: IObj): Unit =
    base = globals.getOrElse("base")(Defaults.Directories.base)
    for (k, v) <- conf do
      v match
        case v: MObj if v.contains("values") && v.contains("scope") =>
          val o = v.getOrElse("values")(MObj())

          val s = v.getOrElse("scope")(MObj())
          val p = s.getOrElse("path")("")
          val r = s.getOrElse("type")("")

          scopes += (p, r) -> o
        case _ => ()

  /** Given all the configurations for the given file and the scope type */
  def getDefaults(file: String, stype: String) =
    val obj = MObj()
    for (k, v) <- scopes do
      if (file.contains(k._1) || file.contains(base + k._1)) &&
        (stype == k._2 || stype == "")
      then obj update v
    obj
