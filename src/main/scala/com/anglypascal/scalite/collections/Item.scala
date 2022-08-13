package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.{Reader, Layout}
import com.anglypascal.scalite.converters.Converters

import com.rallyhealth.weejson.v1.Obj

abstract class Item(filepath: String, globals: DObj) extends Reader(filepath):
  /** */
  def locals: DObj
  def render: String

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
    else Converters.convert(main_matter, filepath)
