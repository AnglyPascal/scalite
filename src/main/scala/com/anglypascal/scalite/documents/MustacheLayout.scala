package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName, Data, DObj}

import com.rallyhealth.weejson.v1.{Obj, Str}
import com.anglypascal.mustache.Mustache
import scala.collection.mutable.LinkedHashMap
import scala.reflect.ClassTag
import com.typesafe.scalalogging.Logger

/** Defines a mustache template. Can have one parent layout. Takes the partials
  * from the "/_includes" folder, the contents from any document with this
  * layout as a parent, and the local and global variables, and renders the
  * template.
  *
  * @param name
  *   name of the layout, which will be refered by other documents
  * @param layoutPath
  *   path to the layout file inside "/_layouts"
  *
  * TODO: Layouts might have locally specified theme
  */
class MustacheLayout(name: String, layoutPath: String)
    extends Layout(name, layoutPath):

  /** Set the parent layout if it's specified in the front matter
    */
  private var _parent: Option[Layout] = None
  def parent = _parent

  private val logger = Logger("Mustache Layout")

  /** Take a list of layouts, and find the parent layout
    */
  def setParent(layouts: Map[String, Layout]): Unit =
    _parent =
      if front_matter.obj.contains("layout") then
        front_matter("layout") match
          case s: Str =>
            logger.debug(s"This layout has a parent layout named $s")
            val pn = s.str
            layouts.get(pn)
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
    *   a weejson object cointaing the following keys:
    *   - '''site''': global variables defined by "\_config.yml"
    *   - '''page''' of '''post''': page or post specific variables
    *   - '''content''': contents of the child file
    * @param partials
    *   the partial layouts defined in the "/\_includes" folder
    * @return
    *   the string returned by the mustache after rendering
    */
  def render(context: DObj): String =
    val str = mustache.render(context, MustacheLayout.mustachePartials)
    parent match
      case Some(p) =>
        logger.debug("Rendering the parent layout now.")
        context.content = str
        p.render(context)
      case _ => str

/** Defines methods to process all the layouts from the "/\_layouts" directory
  */
object MustacheLayout:

  /** prevents the call of layouts before doing the apply */
  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  private val logger = Logger("MustacheLayout companion object")

  // TODO: potentially might not work :p will have to test intensively
  val mustachePartials: Map[String, Mustache] =
    def filter(data: Map[String, Layout])(implicit
        ev: ClassTag[MustacheLayout]
    ) = data collect { case (s, t): (String, MustacheLayout) => (s, t) }
    filter(Partial.partials).map((s, l) => (s, l.mustache))

  /** Process all the layouts in "/\_layouts" directory. This is done in two
    * passes. The first pass creates the layouts without specifying the parents.
    * The second pass assigns to each layout it's parent, if specified in the
    * front matter.
    */
  def apply(directory: String): Map[String, Layout] =
    val files = getListOfFiles(directory)
    val ls = files
      .map(f => {
        val fn = getFileName(f)
        (fn, new MustacheLayout(getFileName(f), f))
      })
      .toMap

    logger.debug("Got the layouts: " + ls.map(_._2.name).mkString(", "))
    ls.map((s, l) => l.setParent(ls))
    _layouts = ls.toMap
    layouts
