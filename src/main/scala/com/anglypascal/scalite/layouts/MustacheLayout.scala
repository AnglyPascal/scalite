package com.anglypascal.scalite.layouts

import com.anglypascal.mustache.Mustache
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.layouts.helpers.*

class ScaliteMustache(temp: String)
    extends Mustache(temp)
    with SlugHelper
    with DateHelper
    with URLHelper

/** Defines a mustache template. Can have one parent layout. Takes the partials
  * from the "/\_includes" folder, the contents from any document with this
  * layout as a parent, and the local and global variables, and renders the
  * template.
  *
  * @param name
  *   name of the layout, which will be refered by other documents
  * @param layoutPath
  *   path to the layout file inside "/\_layouts"
  */
class MustacheLayout(
    name: String,
    val parentDir: String,
    val relativePath: String,
    private val partials: Map[String, Mustache]
) extends Layout("mustache", name):

  private val logger = Logger("Mustache Layout")

  /** The mustache object for this layout */
  private lazy val mustache = ScaliteMustache(mainMatter)

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
  protected[layouts] def justRender(
      context: DObj,
      contentPartial: String = ""
  ): String =
    val str = mustache.render(
      context,
      partials + ("content" -> ScaliteMustache(contentPartial))
    )
    parent match
      case Some(p) =>
        logger.debug("Rendering the parent layout now.")
        p.justRender(context, str)
      case _ => str

/** Defines methods to process all the layouts from the "/\_layouts" directory
  */
class MustacheLayouts(
    val layoutsDir: String,
    val partialsDir: String,
    val ext: String
) extends LayoutGroup:

  val lang = "mustache"

  private val logger = Logger("MustacheLayouts")

  DataAST.init()

  def layouts =
    val ls = layoutFiles
      .filter(matches(_))
      .map(f => {
        val fn = getFileName(f)
        (fn, new MustacheLayout(fn, layoutsDir, f, partials))
      })
      .toMap

    ls.map((s, l) => l.setParent(ls))
    logger.debug("Found layouts: " + ls.map(_._2.toString).mkString(", "))
    ls.toMap

  private lazy val partials: Map[String, Mustache] =
    val ls = partialFiles
      .filter(matches(_))
      .map(f => {
        val m =
          com.anglypascal.scalite.utils.DirectoryReader
            .mainMatter(partialsDir + f)
        (getFileName(f), new Mustache(m))
      })
      .toMap

    logger.debug("Found partials: " + ls.map(s => GREEN(s._1)).mkString(", "))
    ls.toMap

object MustacheGroupConstructor extends LayoutGroupConstructor:
  val lang = "mustache"

  def apply(layoutsDir: String, partialsDir: String, ext: String): LayoutGroup =
    new MustacheLayouts(layoutsDir, partialsDir, ext)
