package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DateParser.dateParseObj
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.StringProcessors.titleParser
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.hooks.ItemHooks
import com.anglypascal.scalite.documents.Convertible

/** Elements that don't have a separate Page, but may be rendered as part of a
  * different Page.
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
class ItemLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    protected val globals: IObj,
    private val collection: IObj
) extends Element
    with Convertible(parentDir + relativePath):

  private val logger = Logger(s"ItemLike \"${CYAN(rType)}\"")
  logger.debug("source: " + GREEN(filepath))

  // by default ItemLike objects don't have layouts
  protected lazy val layoutName =
    extractChain(frontMatter, collection)("layout")("")

  private val beforeInits = ItemHooks.beforeInits(globals)(
    IObj(
      "rType" -> rType,
      "parentDir" -> parentDir,
      "relativePath" -> relativePath
    )
  )

  /** Title of this item */
  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        titleParser(filepath).getOrElse(filename)
      )
    )

  private lazy val _locals =
    val dateString = frontMatter.extractOrElse("date")(filename)
    val dateFormat =
      extractChain(frontMatter, collection, globals)(
        "dateFormat"
      )(Defaults.dateFormat)
    val obj = beforeInits update dateParseObj(dateString, dateFormat)

    val mobj = MObj(
      "title" -> title,
      "parentDir" -> parentDir,
      "relativePath" -> relativePath,
      "rType" -> rType,
      "modifiedTime" -> lastModifiedTime(filepath, dateFormat),
      "filename" -> filename,
      "collection" -> collection.getOrElse("name")("statics")
    )

    obj update frontMatter
    obj update mobj

  lazy val locals =
    _locals += "content" -> render()
    _locals update ItemHooks.beforeLocals(globals)(IObj(_locals))
    IObj(_locals)

  val visible: Boolean = frontMatter.extractOrElse("visible")(true)

  private inline def convert: String =
    if shouldConvert then convert(mainMatter)
    else mainMatter

  /** If there's some frontMatter, then the mainMatter will be conerted with
    * appropriate converter. Then the converted string will be processed with
    * the layout if it exists. Otherwise the unprocessed string will be returned
    */
  protected def render(up: IObj = IObj()): String =
    val str = convert
    val context =
      val c = MObj(
        "site" -> globals,
        "item" -> _locals
      )
      c update ItemHooks.beforeRenders(globals)(IObj(c))
      c update up
      IObj(c)

    val ren = render(str, context)
    ItemHooks.afterRenders(globals)(locals, ren)

  override def toString(): String = CYAN(title)

/** Constructor for ItemLike objects */
object ItemConstructor extends ElemConstructor:

  val styleName = "item"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: IObj,
      collection: IObj
  ): Element =
    ItemLike(rType)(parentDir, relativePath, globals, collection)
