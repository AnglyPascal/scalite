package com.anglypascal.scalite.documents

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.hooks.PageHooks
import com.anglypascal.scalite.utils.DirectoryReader.writeTo
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap

/** A Page of the website. It's a Renderable. So it can be rendered into an HTML
  * string.
  *
  * It has a destination filepath on the disk, where the contents of this Page
  * be written. It has an outputExt, the extension of the output file.
  *
  * The Page provides a write() method to carry out the action of writing the
  * contents returned by the render method to the filepath.
  *
  * It also has a permalink, which is the relative path of this Page in the
  * website.
  *
  * Every page adds itself to the collection of pages in the Pages object. These
  * Pages are mapped against their filepath, which be used to cross refer other
  * pages of this website.
  */
trait Page:
  this: Renderable =>

  private val logger = Logger("Page writer")

  /** FIXME wth? why is it here doing nothing? */
  // PageHooks.beforeInits(globals)(IObj(configs))

  /** Unique identifier to map this page to, in order for the cross reference to
    * work.
    */
  lazy val identifier: String

  /** Relative permanent link to this page */
  lazy val permalink: String

  /** The extension of the output file */
  protected lazy val outputExt: String // this will have to be in urlObj, no?

  /** Method to write the content returned by the render method to the output
    * file at a relative path given by the relative permalink.
    *
    * @param dryRun
    *   Is this a dryRun? If so, don't actually write the Page to the disk.
    *   Otherwise, write it to the filepath.
    */
  def write(dryRun: Boolean = false): Unit =
    if !visible then return
    val path =
      if permalink.endsWith(outputExt) then permalink
      else permalink + outputExt

    if !dryRun then
      logger.debug(s"writing $this to $path")
      val up = PageHooks.beforeRenders(globals)(locals)
      val r = PageHooks.afterRenders(globals)(locals, render(IObj(up)))
      writeTo(path, r)
      PageHooks.afterWrites(globals)(this)
    else logger.debug(s"would write $this to $path")

  // Add this page to the pages collection.
  if visible then Pages.addPage(this)

  // protected def cache(): Unit

/** Holds reference to all the pages of this website.
  *
  * Provides a method to add a new Page to this collection.
  *
  * Also provides a method to find a Page in this collection, given either the
  * relative path to the page in the source directory, or the absolute path to
  * the source file
  */
object Pages:

  private var base: String = Defaults.Directories.base

  private val pages = LinkedHashMap[String, Page]()

  /** Add the given page to the pages collection */
  def addPage(page: Page) =
    pages += page.identifier.stripPrefix(base) -> page

  /** Find a page.
    *
    * @param path
    *   the absolute path or the relative path to the source path of the page
    * @returns
    *   Option containing the found Page
    */
  def findPage(path: String): Option[Page] =
    pages.get(path) orElse pages.get(path.stripPrefix(base))

  /** Provide this object with the base source path */
  def setup(_base: String) = base = _base

  def reset(): Unit =
    pages.clear()
