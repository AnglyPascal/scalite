package com.anglypascal.scalite.documents

import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.utils.DirectoryReader.writeTo
import com.anglypascal.scalite.hooks.PageHooks
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.Defaults
import scala.collection.mutable.LinkedHashMap

trait OtherPage extends Renderable:

  private val logger = Logger("Page writer")

  lazy val identifier: String

  lazy val permalink: String

  protected lazy val outputExt: String

  abstract override protected def render(
      content: String,
      context: DObj
  ): String =
    val c = PageHooks.beforeRenders(globals)(context)
    val s = super.render(content, DObj(c))
    PageHooks.afterRenders(globals)(locals, s)

  def write(dryRun: Boolean = false): Unit =
    if !visible then return
    val path =
      if permalink.endsWith(outputExt) then permalink
      else permalink + outputExt

    if !dryRun then
      logger.debug(s"writing $this to $path")
      val up = PageHooks.beforeRenders(globals)(locals)
      val r = PageHooks.afterRenders(globals)(locals, render(DObj(up)))
      writeTo(path, r)
      // PageHooks.afterWrites(globals)(this)
    else logger.debug(s"would write $this to $path")

  // Add this page to the pages collection.
  if visible then OtherPages.addPage(this)

object OtherPages:

  private var base: String = Defaults.Directories.base

  private val pages = LinkedHashMap[String, OtherPage]()

  /** Add the given page to the pages collection */
  def addPage(page: OtherPage) =
    pages += page.identifier.stripPrefix(base) -> page

  /** Find a page.
    *
    * @param path
    *   the absolute path or the relative path to the source path of the page
    * @returns
    *   Option containing the found Page
    */
  def findPage(path: String): Option[OtherPage] =
    pages.get(path) orElse pages.get(path.stripPrefix(base))

  /** Provide this object with the base source path */
  def setup(_base: String) = base = _base

  def reset(): Unit =
    pages.clear()
