package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DNum
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.documents.Layouts
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.cmpOpt

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  *
  * @tparam A
  *   subclass of [[com.anglypascal.scalite.collections.Item]]
  */
trait Collection[A <: Item](itemConstructor: ItemConstructor[A])
    extends Plugin
    with Page:

  /** Name of the collection */
  val name: String

  protected val parentName = name

  /** Set of posts or other elements for use in context for rendering pages. */
  def items = _items
  private var _items: Map[String, A] = _

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs.
    *
    * @param directory
    *   where files containting items of this collection will be
    * @param globals
    *   global configs
    */
  def setup(directory: String, _globals: DObj) =
    globals = _globals
    val files = getListOfFilepaths(directory)
    def f(fn: String) =
      (getFileName(fn), itemConstructor(directory, fn, globals, locals))
    _items = files.filter(Converters.hasConverter).map(f).toMap

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs, set the sortBy and toc variables, and receive
    * local variables for the rendering of this collection page.
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
  private var permalinkTemplate: String = Defaults.permalinkTemplate
  protected lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  /** Sort the items of this collection by this key */
  protected[this] var sortBy: String = Defaults.Collection.sortBy

  /** Should this collection have a separate page? */
  private var _visible: Boolean = Defaults.Collection.toc
  lazy val visible = _visible

  /** Collection metadata other than sortBy, toc, folder, directory, output. */
  protected var locals: DObj = DObj()

  /** The toc will have default output extension html */
  protected lazy val outputExt = ".html"

  /** Store a reference to the global configs */
  protected var globals: DObj = _

  /** Compare two given items by the given key */
  private def compareBy(fst: A, snd: A, key: String): Int =
    val s = cmpOpt(fst.locals.getStr, fst.locals.getStr)
    if s != 0 then return s
    val n = cmpOpt(fst.locals.getNum, fst.locals.getNum)
    if n != 0 then return n
    0

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  protected def compare(fst: A, snd: A): Boolean =
    val c = compareBy(fst, snd, sortBy)
    if c != 0 then return c < 0
    compareBy(fst, snd, "title") < 0

  protected lazy val render: String =
    parent match
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
  private[collections] def process(): Unit =
    for (_, item) <- items do
      item match
        case item: Page => item.write()
        case _          => ()
    write()
