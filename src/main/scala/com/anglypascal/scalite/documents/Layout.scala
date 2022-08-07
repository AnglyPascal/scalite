package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName}

import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1.{Obj, Str}
import scala.collection.mutable.LinkedHashMap

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
class Layout(val name: String, layoutPath: String) extends Reader(layoutPath):

  /** Set the parent layout if it's specified in the front matter
    */
  private var _parent: Option[Layout] = None
  def parent = _parent

  /** Take a list of layouts, and find the parent layout
    */
  def setParent(layouts: Map[String, Layout]): Unit =
    _parent =
      if front_matter.obj.contains("layout") then
        front_matter("layout") match
          case s: Str =>
            val pn = s.str
            layouts.get(pn)
          case _ => None
      else None

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
  def render(context: Obj, partials: Map[String, Layout]): String =
    val p = partials.map((s, l) => (s, l.mustache))
    val str = mustache.render(context, p)
    parent match
      case Some(p) =>
        context("content") = str
        p.render(context, partials)
      case _ => str

/** Defines methods to process all the layouts from the "/\_layouts" directory
  */
object Layout:

  /** prevents the call of layouts before doing the apply
    */
  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

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
        (fn, new Layout(getFileName(f), f))
      })
      .toMap

    ls.map((s, l) => l.setParent(ls))
    _layouts = ls.toMap
    layouts
