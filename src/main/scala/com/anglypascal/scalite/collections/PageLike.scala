package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.plugins.PageHooks
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.StringProcessors.slugify
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
    protected val globals: IObj,
    private val collection: IObj
) extends Element
    with Page:

  private val logger = Logger(s"PageLike \"${CYAN(rType)}\"")
  logger.debug("source: " + GREEN(filepath))

  protected val configs = MObj(
    "rType" -> rType,
    "parentDir" -> parentDir,
    "relativePath" -> relativePath
  )

  /** Name of the parent layout. Can be set in either the frontMatter, in the
    * scoped defaults, in collection configurations, or the "page" layout, in
    * order of precedence.
    */
  protected lazy val layoutName: String =
    extractChain(frontMatter, collection)("layout")(rType)

  /** Title of this page, can be specified in the frontMatter under key "title",
    * "name" or will simply be the filename
    */
  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(filename)
    )

  /** Local variables publicly visible, used to render the parent template */
  lazy val locals: IObj =
    val l = _locals
    if frontMatter.getOrElse("showExcerpt")(false) then
      l += "excerpt" -> excerpt
    IObj(l)

  private def _locals =
    val dateFormat =
      extractChain(frontMatter, collection, globals)("dateFormat")(
        Defaults.dateFormat
      )
    val mobj = MObj(
      "title" -> title,
      "parentDir" -> parentDir,
      "relativePath" -> relativePath,
      "rType" -> rType,
      "outputExt" -> outputExt,
      "modifiedTime" -> lastModifiedTime(filepath, dateFormat),
      "filename" -> filename,
      "collection" -> collection.getOrElse("name")("statics"),
      "slugTitle" -> slugify(title)
    )

    val nobj = PageHooks.beforeLocals
      .collect(_.apply(globals)(IObj(mobj)))
      .foldLeft(MObj())(_ update _)

    mobj update nobj

  /** Extract excerpt from the mainMatter */
  private lazy val excerpt: String =
    val separator =
      extractChain(frontMatter, globals)("separator")(Defaults.separator)
    Excerpt(
      mainMatter,
      filepath,
      shouldConvert,
      separator
    )(IObj(_locals), globals).content

  /** Relative permanent link to this page in the website */
  lazy val permalink =
    val permalinkTemplate =
      extractChain(frontMatter, collection, globals)(
        "permalink"
      )(Defaults.Statics.permalink)
    purifyUrl(URL(permalinkTemplate)(locals))

  /** The output extension of this page */
  protected lazy val outputExt =
    frontMatter.getOrElse("outputExt")(Converters.findOutputExt(filepath))

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
    val str =
      if shouldConvert then Converters.convert(mainMatter, filepath)
      else mainMatter
    layout match
      case Some(l) =>
        val context = IObj(
          "site" -> globals,
          "page" -> locals,
          "collectionItems" -> CollectionItems.collectionItems
        )
        l.renderWrap(context, str)
      case None => str

  /** Should this page be visible to the site? */
  val visible: Boolean = frontMatter.getOrElse("visible")(true)

  override def toString(): String =
    Console.CYAN + title + Console.RESET

/** Constructor for PageLike objects */
object PageConstructor extends ElemConstructor:

  val styleName = "page"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: IObj,
      collection: IObj
  ): Element =
    new PageLike(rType)(parentDir, relativePath, globals, collection)
