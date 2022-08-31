package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DNum
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.cmpOpt
import scala.collection.parallel.CollectionConverters._

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import com.typesafe.scalalogging.Logger

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  */
class Collection(
    elemCons: ElemConstructor,
    val name: String,
    protected val layoutName: String
) extends Page
    with Plugin: // FIXME obsolete

  private val logger = Logger(s"$name collection")

  /** Set of posts or other elements for use in context for rendering pages. */
  def items = _items
  private var _items: Map[String, Element] = _

  private var constructor = elemCons(name)

  lazy val filepath = s"/collections/$name"

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs.
    *
    * @param directory
    *   where files containting items of this collection will be
    * @param globals
    *   global configs
    */
  def setup(directory: String, _globals: DObj) =
    logger.debug(s"collecting $name from $directory")
    globals = _globals
    val files = getListOfFilepaths(directory)
    logger.debug(s"found ${files.length} files in $directory")
    def f(fn: String) =
      (getFileName(fn), constructor(directory, fn, globals, locals))
    _items = files.filter(Converters.hasConverter).map(f).toMap

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs, set the sortBy and toc variables, and receive
    * local variables for the rendering of this collection page.
    *
    * TODO: i think there should be a better sortby option. there should be a
    * categorize option, that asks for a field in posts oject, and will
    * categorize the posts based on that field in this toc page.
    */
  def setup(
      directory: String,
      _globals: DObj,
      _sortBy: String,
      _toc: Boolean,
      _permalinkTemplate: String,
      _locals: DObj
  ): Unit =
    _visible = _toc
    sortBy = _sortBy
    // We don't need the collections section of globals to render this collection
    // The necessary info is already in locals
    globals = _globals.removed("collection")
    permalinkTemplate = _permalinkTemplate
    locals = _locals
    this.setup(directory, _globals)

  /** Template for the permalink. This will override the permalink template for
    * the entire collection.
    */
  private var permalinkTemplate: String = Defaults.permalink
  lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  /** Sort the items of this collection by this key */
  protected var sortBy: String = Defaults.Collection.sortBy

  /** Should this collection have a separate page? */
  private var _visible: Boolean = Defaults.Collection.toc
  lazy val visible = _visible

  /** Collection metadata other than sortBy, toc, folder, directory, output. */
  protected var locals: DObj = DObj()

  /** The toc will have default output extension html */
  protected lazy val outputExt = ".html"

  /** Store a reference to the global configs */
  protected var globals: DObj = _

  /** Compare two given items by the given key 
   *
   *  TODO WTF is this compareBy function? :|
   *  */
  private def compareBy(fst: Element, snd: Element, key: String): Int =
    // val s = cmpOpt(fst.locals.get(key), fst.locals.get(key))
    val s = cmpOpt(fst.locals.get(key), fst.locals.get(key))
    if s != 0 then return s
    val n = cmpOpt(fst.locals.get("title"), fst.locals.get("title"))
    if n != 0 then return n
    0

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  protected def compare(fst: Element, snd: Element): Boolean =
    val c = compareBy(fst, snd, sortBy)
    if c != 0 then return c < 0
    compareBy(fst, snd, "title") < 0

  protected lazy val render: String =
    layout match
      case None => ""
      case Some(p) =>
        val sortedItems =
          items.map(_._2).filter(_.visible).toList.sortWith(compare)
        val itemsData = DArr(sortedItems.map(_.locals))
        val _locals = locals.add("title" -> DStr(name), "items" -> itemsData)
        val context = DObj(
          "site" -> globals,
          "page" -> _locals
        )
        p.render(context)

  /** This sorts out the items, renders them, and writes them to the disk */
  protected[collections] def process(dryrun: Boolean = false): Unit =
    for (_, item) <- items.par do
      item match
        case item: Page => item.write(dryrun)
        case _          => ()
    write()

  /** TODO: add config options to ask to cache the collection instead of writing
    * it
    */
  protected[collections] def cache(): Unit = ???

  override def toString(): String =
    "\n" + items.map((_, v) => "  " + v.toString).mkString("\n")
