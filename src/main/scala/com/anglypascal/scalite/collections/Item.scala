package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.documents.{Reader, Layout}
import com.anglypascal.scalite.converters.Converter

import com.rallyhealth.weejson.v1.Obj

abstract class Item(filepath: String, globals: Obj) extends Reader(filepath):

  /** */
  def locals: Obj

  def render(partials: Map[String, Layout]): String

class GenericItem(val name: String, filepath: String, globals: Obj)
    extends Item(filepath, globals):

  val title: String =
    front_matter.getOrElse("title")(
      titleParser(filepath).getOrElse("untitled" + this.toString)
    ) // so that titles are always different for different items

  /** check with jekyll if it needs more basic */
  private val _locals =
    val used = List("title")
    val obj = Obj()
    for
      (s, v) <- front_matter.obj
      if !used.contains(s)
    do obj(s) = v
    obj.obj ++= List("title" -> title)
    obj

  def locals = _locals.hardCopy.asInstanceOf[Obj]

  /** TODO: Also provide support for forced conversion. In the globals, a user
    * should be able to say, convert: true to force this conversion here.
    */
  def render(partials: Map[String, Layout]): String =
    if front_matter == Obj() then main_matter
    else
      Converter.convert(main_matter, filepath) match
        case Right(s) => s
        case Left(e)  => throw e

object GenericItem extends Collection[Item]:

  def things = _items
  private var _items: Map[String, Item] = _

  val name = name // TODO: figure out naming convnetion

  def apply(nm: String, directory: String, globals: Obj): Map[String, Item] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new GenericItem(nm, fn, globals)
      (post.title, post)
    _items = files.map(f).toMap

    things
