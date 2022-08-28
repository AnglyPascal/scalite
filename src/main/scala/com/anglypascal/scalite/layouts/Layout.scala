package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.data.DObj
import com.rallyhealth.weejson.v1.Str
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.Set
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DirectoryReader.{getListOfFilepaths}

/** Defines an abstract Layout.
  *
  * @constructor
  *   returns a new Layout
  * @param name
  *   name of the layout, which will be referred to in the front matters
  * @param layoutPath
  *   path to the layout file
  */
abstract class Layout(
    val lang: String,
    val name: String,
    layoutDir: String,
    layoutPath: String
) extends Reader(layoutDir + layoutPath):

  /** */
  private val logger = Logger("Mustache Layout")

  /** Render the layout with the given Data object as context
    *
    * @param context
    *   a DObj with values of all the placeholders and global variables.
    * @param contentPartial
    *   The partial string that needs to be rendered under the "content" tag
    * @return
    *   the rendered layout as a string
    */
  def render(context: DObj, contentPartial: String = ""): String

  /** Parent of this layout, specified in the front matter */
  def parent: Option[Layout] = _parent
  private var _parent: Option[Layout] = None

  /** Take a list of layouts, and find the parent layout */
  def setParent(layouts: Map[String, Layout]): Unit =
    _parent =
      if front_matter.obj.contains("layout") then
        front_matter("layout") match
          case s: Str =>
            logger.trace(s"layout $name has a parent layout named $s")
            val pn = s.str
            layouts.get(pn) match
              case Some(v) =>
                v match
                  case v: MustacheLayout => Some(v)
                  case _                 => None
              case None =>
                logger.trace(s"parent layout $pn of layout $name doesn't exist")
                None
          case _ =>
            logger.trace("layout field of the front matter must be a string")
            None
      else
        logger.trace(s"layout $name doesn't have a specified parent layout")
        None

  override def toString(): String =
    Console.GREEN + name + Console.RESET +
      parent
        .map(Console.YELLOW + " -> " + Console.GREEN + _.toString)
        .getOrElse("") + Console.RESET

/** Layout Object holding all the defined LayoutObjects. During construction
  * with apply(), it fetches all the files in layoutPath and uses the
  * appropriate Layout constructor to create a layout representing that file.
  */
object Layouts:

  private val layoutConstructors: Set[LayoutObject] = Set()

  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  private val logger = Logger("Layout object")

  /** Add a new layout constructor to this set */
  def addEngine(engine: LayoutObject) = layoutConstructors += engine

  /** Fetch all the layouts and partials in the given paths. */
  def apply(layoutsDir: String, partialsDir: String) =
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
        logger.debug(s"Layout named $name not found")
        None
      case some => some

  override def toString(): String =
    layouts
      .map((k, v) =>
        v.lang + ": " + Console.RED + k
          + Console.YELLOW + " -> " + v.toString
      )
      .mkString("\n")

/** A trait for generic layout object. Specifies which files this layout will
  * match, and how it will create layouts from the files in the given
  * directories.
  */
trait LayoutObject extends Plugin:

  /** Get the layouts of this type */
  def layouts: Map[String, Layout]

  /** The extensions of the files this converter is able to convert */
  def ext: util.matching.Regex

  /** Does this constructor recognize this filepath? */
  def matches(filepath: String): Boolean =
    ext.matches(filepath)

  /** Create a layout this constructor matches from the given directories */
  def createLayouts(
      layoutsDir: String,
      partialsDir: String,
      layoutFiles: Array[String],
      partialFiles: Array[String]
  ): Map[String, Layout]
