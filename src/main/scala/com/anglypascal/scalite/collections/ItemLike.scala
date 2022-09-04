package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.titleParser
import com.typesafe.scalalogging.Logger

class ItemLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    globals: IObj,
    collection: IObj
) extends Element:

  private val logger = Logger(s"ItemLike \"${CYAN(rType)}\"")
  logger.debug("creating from " + GREEN(filepath))

  protected val layoutName = ""

  /** Title of this item */
  val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        titleParser(filepath).getOrElse("untitled" + this.toString)
      )
    ) // so that titles are always different for different items

  // TODO: check with jekyll if it needs more default variables
  lazy val locals =
    val obj = MObj()
    for (s, v) <- frontMatter do obj(s) = v
    obj update MObj("title" -> title)
    IObj(obj)

  val visible: Boolean = frontMatter.extractOrElse("visible")(false)

  /** If there's some front\_matter, then the main\_matter will be conerted with
    * appropriate converter. Otherwise, the identity will be returned
    */
  protected lazy val render: String =
    // TODO what if frontmatter is deleted by the process
    if frontMatter.obj.isEmpty then mainMatter
    else Converters.convert(mainMatter, filepath)

  override def toString(): String = CYAN(title)

object ItemConstructor extends ElemConstructor:

  val styleName = "item"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: IObj,
      collection: IObj
  ): Element =
    ItemLike(rType)(parentDir, relativePath, globals, collection)
