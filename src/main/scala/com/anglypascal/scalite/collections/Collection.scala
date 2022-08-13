package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Layouts

import com.anglypascal.scalite.documents.Page

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  */
trait Collection[A] extends Page: // TODO: need to supply some metadata

  /** Name of the collection */
  val name: String

  protected val parent_name: String = name

  /** Set of posts or other elements for use in context for rendering pages. */
  /** TODO Do we need it to be a map? */
  def things: Map[String, A]

  /** This sorts out the items, renders them, and writes them to the disk */
  def process: Unit

  def apply(
      directory: String,
      globals: DObj,
  ): Map[String, A]

  def apply(
      directory: String,
      _locals: DObj,
      globals: DObj,
      _sortBy: String = "title",
      _toc: Boolean = false 
      // TODO: for this option, we need to add Page :cold_sweat:
  ): Map[String, A] = 
    sortBy = _sortBy
    toc = _toc
    locals = _locals
    apply(directory, locals, globals)

  private var _sortBy = "title"
  def sortBy = _sortBy
  def sortBy_=(key: String) = _sortBy = key

  private var _toc = false
  def toc = _toc
  def toc_=(t: Boolean) = _toc = t

  private var _locals: DObj = _
  def locals = _locals
  def locals_=(loc: DObj) = _locals = loc

  /** TODO: sort things by sortBy
    */
  def compare(fst: A, snd: A): Int // = this.date compare that.date

  Collections.addToCollection(this)

