package com.anglypascal.scalite.layouts

import scala.collection.mutable.Set

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths

import com.typesafe.scalalogging.Logger

/** Layout Object holding all the defined LayoutObjects. During construction
  * with apply(), it fetches all the files in layoutPath and uses the
  * appropriate Layout constructor to create a layout representing that file.
  */
object Layouts extends Configurable:

  val sectionName: String = "layouts"

  private val layoutConstructors: Set[LayoutObject] = Set()

  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  private val logger = Logger("Layout object")

  /** Add a new layout constructor to this set */
  def addEngine(engine: LayoutObject) = layoutConstructors += engine

  def apply(configs: MObj, globals: IObj): Unit = 
    val layoutsDir = 
      globals.getOrElse("base")(Defaults.Directories.base) + 
      globals.getOrElse("layoutsDir")(Defaults.Directories.layoutsDir)
    val partialsDir = 
      globals.getOrElse("base")(Defaults.Directories.base) + 
      globals.getOrElse("partialsDir")(Defaults.Directories.partialsDir)
    val layoutFiles = getListOfFilepaths(layoutsDir)
    val partialFiles = getListOfFilepaths(partialsDir)
    _layouts = Map(
      layoutConstructors
        .flatMap(
          _.createLayouts(
            layoutsDir,
            partialsDir,
            layoutFiles,
            partialFiles
          ).toList
        )
        .toList: _*
    )

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
