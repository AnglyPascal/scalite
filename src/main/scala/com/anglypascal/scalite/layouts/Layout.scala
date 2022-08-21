package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.data.DObj
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
abstract class Layout(val name: String, layoutPath: String)
    extends Reader(layoutPath):

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

  /** Parent of this layout, specified in the front matter
    */
  def parent: Option[Layout]

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
  def apply(layoutsPath: String, partialsPath: String) =
    val layoutFiles = getListOfFilepaths(layoutsPath)
    val partialFiles = getListOfFilepaths(partialsPath)
    _layouts = Map(
      layoutConstructors
        .flatMap(_.createLayouts(layoutFiles, partialFiles).toList)
        .toList: _*
    )

  def get(name: String): Option[Layout] =
    layouts.get(name) match
      case None =>
        logger.debug(s"Layout named $name not found")
        None
      case some => some

/** A trait for generic layout object. Specifies which files this layout will
  * match, and how it will create layouts from the files in the given
  * directories.
  */
trait LayoutObject extends Plugin:

  /** The extensions of the files this converter is able to convert */
  def ext: util.matching.Regex

  /** Does this constructor recognize this filepath? */
  def matches(filepath: String): Boolean =
    ext.matches(filepath)

  /** Create a layout this constructor matches from the given directories */
  def createLayouts(
      layoutFiles: Array[String],
      partialFiles: Array[String]
  ): Map[String, Layout]
