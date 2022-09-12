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
    private val mainMatter: String,
    val filepath: String,
    private val shouldConvert: Boolean,
    private val separator: String
)(
    private val _locals: DObj,
    private val globals: DObj
) extends Renderable:

  private val logger = Logger("Excerpt")

  val visible: Boolean = true

  private lazy val rawContent =
    val mkdnLinkRef = """(?m)^ {0,3}(?:(\[[^\]]+\])(.+))$""".r
    val Array(head, tail) = mainMatter.split(separator, 2)
    head + "\n\n" + mkdnLinkRef.findAllMatchIn(mainMatter).mkString("\n")

  lazy val locals = _locals

  protected val layoutName: String = "empty"

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

  override def toString(): String =
    "Excerpt: " + shouldConvert // + " and string: " + render
