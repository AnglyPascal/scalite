package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName}
import com.anglypascal.scalite.data.DObj 
import com.anglypascal.scalite.documents.Reader

import com.rallyhealth.weejson.v1.{Obj, Str}
import com.anglypascal.mustache.Mustache
import com.typesafe.scalalogging.Logger

/** Defines a mustache template. Can have one parent layout. Takes the partials
  * from the "/\_includes" folder, the contents from any document with this
  * layout as a parent, and the local and global variables, and renders the
  * template.
  *
  * TODO: Layouts might have locally specified theme
  *
  * @param name
  *   name of the layout, which will be refered by other documents
  * @param layoutPath
  *   path to the layout file inside "/\_layouts"
  */
class MustacheLayout(name: String, layoutPath: String)
    extends Layout(name, layoutPath):

  /** Set the parent layout if it's specified in the front matter
    */
  private var _parent: Option[Layout] = None
  def parent = _parent

  private val logger = Logger("Mustache Layout")

  /** Take a list of layouts, and find the parent layout */
  def setParent(layouts: Map[String, Layout]): Unit =
    _parent =
      if front_matter.obj.contains("layout") then
        front_matter("layout") match
          case s: Str =>
            logger.debug(s"This layout has a parent layout named $s")
            val pn = s.str
            layouts.get(pn) match
              case Some(v) =>
                v match
                  case v: MustacheLayout => Some(v)
                  case _                 => None
              case None => None
          case _ =>
            logger.error(
              "The specified layout doesn't exist, " +
                "or the specified value couldn't be interpretted as a string"
            )
            None
      else
        logger.debug("This layout doesn't have a specified parent layout")
        None

  /** The mustache object for this layout */
  lazy val mustache = new Mustache(main_matter)

  /** Evaluate the template by rendering it with it's context and partials
    *
    * @param context
    *   a Data object cointaing the following keys:
    *   - '''site''': global variables defined by "\_config.yml"
    *   - '''page''' of '''post''': page or post specific variables
    *   - '''content''': contents of the child file
    * @param partials
    *   the partial layouts defined in the "/\_includes" folder
    * @return
    *   the string returned by the mustache after rendering
    */
  def render(context: DObj): String =
    val str = mustache.render(context, MustacheLayout.partials)
    parent match
      case Some(p) =>
        logger.debug("Rendering the parent layout now.")
        context.content = str
        p.render(context)
      case _ => str

/** Defines methods to process all the layouts from the "/\_layouts" directory
  */
object MustacheLayout extends LayoutObject:

  /** The default mustache layout will only handle files with the .mustache
    * extension
    */
  def ext = raw"(*.mustache|*.html)".r

  /** prevents the call of layouts before doing the apply */
  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  private val logger = Logger("MustacheLayout companion object")

  /** Process all the layouts in "/\_layouts" directory. This is done in two
    * passes. The first pass creates the layouts without specifying the parents.
    * The second pass assigns to each layout it's parent, if specified in the
    * front matter.
    */
  def createLayouts(
      layoutsPath: String,
      partialsPath: String
  ): Map[String, Layout] =
    val files = getListOfFiles(layoutsPath)
    val ls = files
      .filter(matches(_))
      .map(f => {
        val fn = getFileName(f)
        (fn, new MustacheLayout(fn, f))
      })
      .toMap

    _partials = getPartials(partialsPath)
    logger.debug("Got the layouts: " + ls.map(_._2.name).mkString(", "))
    ls.map((s, l) => l.setParent(ls))
    _layouts = ls.toMap
    layouts

  private var _partials: Map[String, Mustache] = _
  def partials = _partials

  /** Process all the partials in "/\_partials" directory. */
  private def getPartials(directory: String): Map[String, Mustache] =
    val files = getListOfFiles(directory)
    val ls = files
      .filter(matches(_))
      .map(f => {
        object R extends Reader(f)
        (getFileName(f), new Mustache(R.main_matter))
      })
      .toMap

    logger.debug("Got the partials: " + ls.map(_._1).mkString(", "))
    _partials = ls.toMap
    partials
