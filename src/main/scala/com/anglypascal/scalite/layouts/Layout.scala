package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.plugins.LayoutHooks
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.documents.SourceFile

/** Defines an abstract Layout. */
trait Layout extends SourceFile:

  val lang: String

  val name: String

  protected val reader: Reader
  protected val shouldConvert = false

  LayoutHooks.beforeInits foreach { _.apply(lang, name, filepath) }

  private val logger = Logger(s"${lang.capitalize} layout")

  /** Render the layout with the given Data object as context
    *
    * @param context
    *   a DObj with values of all the placeholders and global variables.
    * @param content
    *   The string returned by the child on this layout
    * @return
    *   the rendered layout as a string
    */
  def render(context: DObj, content: String = ""): String

  /** Wrapped render function, runs the hooks before and after the underlying
    * layout is rendered.
    */
  def renderWrap(context: DObj, content: String = ""): String =
    LayoutHooks.beforeRenders foreach { _.apply(context, content) }
    val s = render(context, content)
    LayoutHooks.afterRenders.foldLeft(s)((str, hook) => hook(str))

  /** Parent of this layout, specified in the front matter */
  def parent: Option[Layout] = _parent
  private var _parent: Option[Layout] = None

  /** Take a list of layouts, and find the parent layout */
  def setParent(layouts: Map[String, Layout]): Unit =
    val frontMatter = reader.frontMatter
    if frontMatter.contains("layout") then
      val pn = frontMatter.getOrElse("layout")("")
      layouts.get(pn) match
        case Some(v) =>
          v match
            case v: Layout => _parent = Some(v)
            case null      => ()
        case None => logger.trace(s"parent $pn of layout $name doesn't exist")

  override def toString(): String =
    GREEN(name) + parent.map(p => YELLOW(" -> ") + p.toString).getOrElse("")
