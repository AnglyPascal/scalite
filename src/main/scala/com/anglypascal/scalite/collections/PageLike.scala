package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.StringProcessors.slugify
import com.rallyhealth.weejson.v1.Obj
import com.anglypascal.scalite.ScopedDefaults
import com.typesafe.scalalogging.Logger

/** Elements that may be rendered into pages of the website, such as static
  * pages.
  *
  * A PageLike is a Page. So it has a destination filepath relative to the base
  * destination, and it has a write() method that writes the contents returned
  * by the render method to the file at filepath.
  *
  * @param rType
  *   The named type of these Pages. For example, "static"
  * @param parentDir
  *   The directory of this Element's Collection.
  * @param relativePath
  *   The relative path to the file of this Element from the `parentDir`
  * @param globals
  *   The global variables
  * @param collection
  *   The configuration variables for this Element's Collection
  */
class PageLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    private val globals: DObj,
    private val collection: DObj
) extends Element
    with Page:

  private val logger = Logger(s"PageLike $rType")
  logger.debug("creating from " + GREEN(filepath))

  /** Name of the parent layout. Can be set in either the frontMatter, in the
    * scoped defaults, in collection configurations, or the "page" layout, in
    * order of precedence.
    */
  protected val layoutName: String =
    extractChain(frontMatter, collection)("layout")(rType)

  /** Title of this page, can be specified in the frontMatter under key "title",
    * "name" or will simply be the filename
    */
  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(filename)
    )

  /** Local variables publicly visible, used to render the parent template */
  lazy val locals: DObj =
    val dateFormat =
      extractChain(frontMatter, globals)("dateFormat")(Defaults.dateFormat)
    val obj = Obj(
      "title" -> title,
      "outputExt" -> outputExt,
      "modifiedTime" -> lastModifiedTime(filepath, dateFormat),
      "filename" -> filename,
      "collection" -> collection.getOrElse("name")("statics"),
      "slugTitle" -> slugify(title)
    )
    DObj(obj)

  /** Relative permanent link to this page in the website */
  lazy val permalink =
    val permalinkTemplate =
      extractChain(frontMatter, collection, globals)(
        "permalink"
      )(Defaults.Statics.permalink)
    purifyUrl(URL(permalinkTemplate)(locals))

  /** The output extension of this page */
  protected lazy val outputExt =
    frontMatter.getOrElse("outputExt")(
      Converters.findByExt(filepath).map(_.outputExt).getOrElse(".html")
    )

  /** Render the contents of this page to a HTML string
    *
    * TODO: What if the page is a plain HTML file, which need not be converted,
    * but can have a layout file? What if it's a HTML file, and has no layout?
    * The second question is easy, it should just be returned without anything.
    * How do we decide if the mainMatter is HTML or not? FIXME test the
    * Converters
    *
    * One solution is to make sure that truly static pages have the .html
    * extension. That way, the Identity converter will be used.
    */
  protected lazy val render: String =
    val str = Converters.convert(mainMatter, filepath)
    val context = DObj(
      "site" -> globals,
      "page" -> locals
    )
    layout match
      case Some(l) => l.render(context, str)
      case None    => str

  /** Should this page be visible to the site? */
  lazy val visible: Boolean = frontMatter.getOrElse("visible")(true)

  override def toString(): String =
    Console.CYAN + title + Console.RESET

/** Constructor for PageLike objects */
object PageConstructor extends ElemConstructor:

  val styleName = "page"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): Element =
    new PageLike(rType)(parentDir, relativePath, globals, collection)
