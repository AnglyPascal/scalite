package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.DateParser.dateParseObj
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.titleParser
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.Defaults

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
    globals: IObj,
    collection: IObj
) extends Element:

  private val logger = Logger(s"ItemLike \"${CYAN(rType)}\"")
  logger.debug("source: " + GREEN(filepath))

  // by default ItemLike objects don't have layouts
  protected val layoutName =
    extractChain(frontMatter, collection)("layout")("")

  /** Title of this item */
  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        titleParser(filepath).getOrElse("item " + filename)
      )
    )

  lazy val locals =
    val dateString = frontMatter.extractOrElse("date")(filename)
    val dateFormat =
      extractChain(frontMatter, collection, globals)(
        "dateFormat"
      )(Defaults.dateFormat)
    val obj = dateParseObj(dateString, dateFormat)

    obj update frontMatter
    obj += "title" -> title
    obj += "lastModifiedTime" -> lastModifiedTime(filepath, dateFormat)
    obj += "filename" -> filename

    IObj(obj)

  val visible: Boolean = frontMatter.extractOrElse("visible")(false)

  private val shouldConvert = !frontMatter.isEmpty

  /** If there's some frontMatter, then the mainMatter will be conerted with
    * appropriate converter. Then the converted string will be processed with
    * the layout if it exists. Otherwise the unprocessed string will be returned
    */
  protected lazy val render: String =
    val str =
      if shouldConvert then Converters.convert(mainMatter, filepath)
      else mainMatter
    layout match
      case Some(l) =>
        val context = IObj(
          "site" -> globals,
          "item" -> locals
        )
        l.renderWrap(context, str)
      case None => str

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
