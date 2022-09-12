package com.anglypascal.scalite.collections

import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.converters.Converters
import com.typesafe.scalalogging.Logger

/** FIXME: What if the source file in the element is not in Markdown?
  *
  * There also seems to be some missing parts? idk should an excerpt have more
  * opitons? Like a permalink as jekyll does?
  */
class Excerpt(
    private val element: Element,
    private val globals: DObj,
    private val separator: String
) extends Renderable:

  val visible = true

  val filepath: String = element.parentDir + element.relativePath

  val rType = element.rType + "#excerpt"

  private val shouldConvert = element.shouldConvert

  private lazy val rawContent =
    val mkdnLinkRef = raw"^ {0,3}(?:(\[[^\]]+\])(.+))".r
    val mainMatter = element.mainMatter
    val Array(head, tail) = mainMatter.split(separator, 2)
    head + "\n" + mkdnLinkRef.findAllMatchIn(mainMatter).mkString("\n")

  protected val layoutName: String = "empty"

  private val logger = Logger("Excerpt")

  lazy val locals: DObj = element.locals

  protected lazy val render: String =
    val str =
      if shouldConvert then Converters.convert(rawContent, filepath)
      else rawContent
    val context =
      DObj(
        "site" -> globals,
        "page" -> locals,
        "collectionItems" -> CollectionItems.collectionItems
      )
    val rendered = layout match
      case Some(l) =>
        logger.debug(s"$this has layout ${l.name}")
        l.renderWrap(context, str)
      case None =>
        logger.debug(s"$this has no specified layout")
        str
    rendered

  def content = render
