package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths

import scala.collection.mutable.Set
import scala.collection.mutable.Map

import com.typesafe.scalalogging.Logger
import cats.instances.partialOrder

/** Layout Object holding all the defined LayoutObjects. During construction
  * with apply(), it fetches all the files in layoutPath and uses the
  * appropriate Layout constructor to create a layout representing that file.
  */
object Layouts extends Configurable:

  val sectionName: String = "layouts"

  private val constructors = Map[String, LayoutGroupConstructor]()

  private val layouts = Map[String, Layout]()

  private val logger = Logger("Layout object")

  /** TODO: add section in Defaults */
  private val conf = MObj(
    "mustache" -> MObj(
      "layoutsDir" -> Defaults.Directories.layoutsDir,
      "partialsDir" -> Defaults.Directories.partialsDir,
      "ext" -> "mustache,html"
    )
  )

  /** Add a new layout constructor to this set */
  def addEngine(engine: LayoutGroupConstructor) =
    constructors += engine.lang -> engine

  def apply(configs: MObj, globals: IObj): Unit =
    import com.anglypascal.scalite.Defaults.Directories
    conf update configs

    val base = globals.getOrElse("base")(Directories.base)

    for (k, v) <- conf if constructors.contains(k) do
      v match
        case v: MObj =>
          val cons = constructors(k)
          val lD =
            extractChain(v, globals)("layoutsDir")(Directories.layoutsDir)
          val pD =
            extractChain(v, globals)("partialsDir")(Directories.partialsDir)
          val ext = v.getOrElse("ext")("")
          layouts ++= cons(base + lD, base + pD, ext).layouts
        case _ => ()

  def get(name: String): Option[Layout] =
    layouts.get(name) match
      case None =>
        logger.debug(s"layout $name not found")
        None
      case some =>
        logger.debug(s"layout $name found")
        some

  override def toString(): String =
    layouts
      .map((k, v) =>
        v.lang + ": " + Console.RED + k
          + Console.YELLOW + " -> " + v.toString
      )
      .mkString("\n")
