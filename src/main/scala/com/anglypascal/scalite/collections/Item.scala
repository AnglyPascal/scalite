package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.documents.{Reader, Layout}
import com.anglypascal.scalite.converters.Converter

import com.rallyhealth.weejson.v1.Obj

abstract class Item(filepath: String, globals: DObj) extends Reader(filepath):

  /** */
  def locals: DObj

  def render: String

  /** TODO: Custom sorting is specified by the "sort_by" entry inside the global
    * option for this collection. Custom ordering should also be avaiable.
    * Anything that does not match the filenames in the ordering, should come
    * later
    */
  def compare(that: Post): Int // = this.date compare that.date

class GenericItem(filepath: String, globals: DObj)
    extends Item(filepath, globals):

  /** */
  val title: String =
    front_matter.getOrElse("title")(
      front_matter.getOrElse("name")(
        titleParser(filepath).getOrElse("untitled" + this.toString)
      )
    ) // so that titles are always different for different items

  /** check with jekyll if it needs more basic */
  def locals =
    val used = List("title")
    val obj = Obj()
    for
      (s, v) <- front_matter.obj
      if !used.contains(s)
    do obj(s) = v
    obj.obj ++= List("title" -> title)
    DObj(obj)

  /** TODO: Also provide support for forced conversion. In the globals, a user
    * should be able to say, convert: true to force this conversion here.
    */
  def render: String =
    if front_matter == Obj() then main_matter
    else Converter.convert(main_matter, filepath)

  def compare(that: Post): Int = ???

/** Defines the collection of generic item */
class GenericCollection(val name: String) extends Collection[Item]:

  def things = _items
  private var _items: Map[String, Item] = Map()

  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val item = new GenericItem(fn, globals)
      (item.title, item)
    _items = files.map(f).toMap
    things

  def render: Unit = ???
