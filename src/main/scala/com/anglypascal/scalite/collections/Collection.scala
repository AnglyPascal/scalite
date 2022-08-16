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

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  */
trait Collection[A <: Item] extends Plugin with Page:

  /** Name of the collection */
  val name: String

  /** Set of posts or other elements for use in context for rendering pages. */
  def items = _items
  def items_=(its: Map[String, A]) = _items = its
  private var _items: Map[String, A] = _

  /** This sorts out the items, renders them, and writes them to the disk
    *
    * TODO This will write to the disk, but to where?
    */
  def process: Unit = ???

  def apply(directory: String, globals: DObj): Unit

  protected val parent_name: String = name

  def apply(
      directory: String,
      _locals: DObj,
      _globals: DObj,
      _sortBy: String = "title",
      _toc: Boolean = false
  ): Unit =
    sortBy = _sortBy
    toc = _toc
    locals = _locals
    globals = _globals
    apply(directory, _globals)

  def sortBy = _sortBy
  def sortBy_=(key: String) = _sortBy = key
  private var _sortBy = "title"

  def toc = _toc
  def toc_=(t: Boolean) = _toc = t
  private var _toc = false

  /** Collection metadata other than sortBy, toc, folder, directory, output. */
  def locals = _locals
  def locals_=(loc: DObj) = _locals = loc
  private var _locals: DObj = _

  def globals = _globals
  def globals_=(glob: DObj) = _globals = glob
  private var _globals: DObj = _

  private def compareBy(fst: A, snd: A, key: String): Int =
    val g1 = fst.locals.get(key)
    val g2 = snd.locals.get(key)
    g1 match
      case None =>
        g2 match
          case None    => 0
          case Some(_) => -1
      case Some(s1): Some[DStr] =>
        g2 match
          case None                 => 1
          case Some(s2): Some[DStr] => s1.str compare s2.str
          case _                    => 0
      case Some(s1): Some[DNum] =>
        g2 match
          case None                 => 1
          case Some(s2): Some[DNum] => s1.num compare s2.num
          case _                    => 0
      case _ => 0

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  protected def compare(fst: A, snd: A): Boolean =
    val c = compareBy(fst, snd, sortBy)
    if c != 0 then return c < 0
    compareBy(fst, snd, "title") < 0

  def render: String =
    if !toc then return ""
    val sortedItems = items.map(_._2).toList.sortWith(compare)
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

  def write(filepath: String): Unit =
    if !toc then return
    Files.write(Paths.get(filepath), render.getBytes(StandardCharsets.UTF_8))
