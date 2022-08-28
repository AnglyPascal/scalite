package com.anglypascal.scalite.layouts

import com.anglypascal.mustache.Mustache
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.Data
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.documents.Reader

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
class MustacheLayout(name: String, layoutDir: String, layoutPath: String)
    extends Layout("mustache", name, layoutDir, layoutPath):


  private val logger = Logger("Mustache Layout")

  /** The mustache object for this layout */
  lazy val mustache = new Mustache(main_matter)

  /** Evaluate the template by rendering it with it's context and partials
    *
    * @param context
    *   a Data object cointaing the following keys:
    *   - '''site''': global variables defined by "\_config.yml"
    *   - '''page''' of '''post''': page or post specific variables
    * @param contentPartial
    *   The partial string that needs to be rendered by mustache under the
    *   "content" tag
    * @return
    *   the string returned by the mustache after rendering
    */
  def render(context: DObj, contentPartial: String = ""): String =
    val str = mustache.render(
      context,
      MustacheLayouts.partials + ("content" -> new Mustache(contentPartial))
    )
    parent match
      case Some(p) =>
        logger.debug("Rendering the parent layout now.")
        p.render(context, str)
      case _ => str

/** Defines methods to process all the layouts from the "/\_layouts" directory
  */
object MustacheLayouts extends LayoutObject with Plugin:

  /** The default mustache layout will only handle files with the .mustache
    * extension
    */
  def ext = "(.*.mustache|.*.html)".r

  /** prevents the call of layouts before doing the apply */
  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  private var _partials: Map[String, Mustache] = _
  def partials = _partials

  private val logger = Logger("MustacheLayouts")

  /** Process all the layouts in "/\_layouts" directory. This is done in two
    * passes. The first pass creates the layouts without specifying the parents.
    * The second pass assigns to each layout it's parent, if specified in the
    * front matter.
    */
  def createLayouts(
      layoutsDir: String,
      partialsDir: String,
      layoutFiles: Array[String],
      partialFiles: Array[String]
  ): Map[String, Layout] =
    val ls = layoutFiles
      .filter(matches(_))
      .map(f => {
        val fn = getFileName(f)
        (fn, new MustacheLayout(fn, layoutsDir, f))
      })
      .toMap

    _partials = getPartials(partialsDir, partialFiles)
    ls.map((s, l) => l.setParent(ls))
    _layouts = ls.toMap
    logger.debug(
      "Got the layouts: " + ls
        .map(_._2.toString)
        .mkString(", ")
    )
    layouts

  /** Process all the partials in "/\_partials" directory. */
  private def getPartials(
      partialsDir: String,
      partialFiles: Array[String]
  ): Map[String, Mustache] =
    val ls = partialFiles
      .filter(matches(_))
      .map(f => {
        object R extends Reader(partialsDir + f)
        (getFileName(f), new Mustache(R.main_matter))
      })
      .toMap

    logger.debug(
      "Got the partials: " + ls
        .map(Console.GREEN + _._1 + Console.RESET)
        .mkString(", ")
    )
    ls.toMap
