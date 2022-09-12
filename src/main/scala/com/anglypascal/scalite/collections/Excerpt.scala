package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.documents.Renderable
import com.typesafe.scalalogging.Logger

/** Excerpts from the given mainMatter
  *
  * By default excerpt is your first paragraph of a doc: everything before the
  * first two new lines:
  *
  * ```
  * ---
  *  title: Example
  * ---
  *
  * First paragraph with [link][1].
  *
  * Second paragraph.
  *
  * [1]: http://example.com/
  * ```
  *
  * This is fairly good option for Markdown and Textile files. But might cause
  * problems for HTML docs (which is quite unusual for Jekyll). If default
  * excerpt delimiter is not good for you, you might want to set your own via
  * configuration option `excerpt_separator`. For example, following is a good
  * alternative for HTML docs:
  *
  * ```
  * file: _config.yml
  * excerpt_separator: "<!-- more -->"
  * ```
  *
  * Notice that all markdown-style link references will be appended to the
  * excerpt. So the example doc above will have this excerpt source:
  *
  * ```
  * First paragraph with [link][1].
  *
  * [1]: http://example.com/
  * ```
  *
  * Excerpts are rendered as the time of the creation of locals
  *
  * FIXME: What if the source file in the element is not in Markdown? How do we
  * get the link references?
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
