package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DNum
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.documents.Layouts
import com.anglypascal.scalite.documents.Page

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.URL

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
  *
  * TODO: Items should receive a reference to the collection it's part of
  */
trait Collection[A <: Item] extends Plugin with Page:

  /** Name of the collection */
  val name: String

  protected val parentName = name

  /** Set of posts or other elements for use in context for rendering pages. */
  def items = _items
  protected def items_=(its: Map[String, A]) = _items = its
  private var _items: Map[String, A] = _

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs.
    *
    * @param directory
    *   where files containting items of this collection will be
    * @param globals
    *   global configs
    */
  def apply(directory: String, _globals: DObj): Unit

  /** Collect all the elements of this collection from the given directory, will
    * the given global configs, set the sortBy and toc variables, and receive
    * local variables for the rendering of this collection page.
    */
  def apply(
      directory: String,
      _locals: DObj,
      _globals: DObj,
      _sortBy: String = "title",
      _toc: Boolean = false,
      _permalink: String = ""
  ): Unit =
    if _toc then _visible = true
    sortBy = _sortBy
    globals = _globals
    permalinkTemplate = _permalink

    // TODO: what else needs to be added to the collection?
    locals = _locals.add("name" -> DStr(name))

    apply(directory, _globals)

  /** Template for the permalink. This will be prepended to the template of the
    * items. TODO
    */
  private var permalinkTemplate: String = _
  lazy val permalink: String = URL(permalinkTemplate)(locals)

  /** Sort the items of this collection by this key */
  protected var sortBy: String = _

  /** Should this collection have a separate page? */
  protected var _visible: Boolean = false
  def visible = _visible

  /** Collection metadata other than sortBy, toc, folder, directory, output. */
  protected var locals: DObj = _

  def outputExt = ".html"

  /** Store a reference to the global configs */
  protected var globals: DObj = _

  /** Compare two options */
  private def cmpOpt[T](
      o1: Option[T],
      o2: Option[T]
  )(using ord: Ordering[T]): Int =
    o1 match
      case None =>
        o2 match
          case None    => 0
          case Some(_) => -1
      case Some(a) =>
        o2 match
          case None    => 1
          case Some(b) => ord.compare(a, b)

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

  protected def render: String =
    val sortedItems = items.map(_._2).filter(_.visible).toList.sortWith(compare)
    val itemsData = DArr(sortedItems.map(_.locals))
    val context = DObj(
      "site" -> globals,
      "page" -> DObj(
        "title" -> DStr(name),
        "items" -> itemsData
      )
    )
    parent match
      case None    => ""
      case Some(p) => p.render(context)

  /** This sorts out the items, renders them, and writes them to the disk
    */
  private[collections] def process(): Unit =
    for (_, item) <- items do item.write()
    write()
