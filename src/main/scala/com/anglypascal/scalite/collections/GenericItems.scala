package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.documents.Layout
import com.anglypascal.scalite.utils.StringProcessors.titleParser
import com.rallyhealth.weejson.v1.Obj
import com.anglypascal.scalite.data.DataExtensions.*

/** A generic implementation of the Item class. Defines title or name of the
  * Item, and depending on whether it has a front matter or not, converters the
  * contents with a Converter
  */
class GenericItem(parentDir: String, relativePath: String, globals: DObj)
    extends Item(parentDir, relativePath, globals):

  /** Title of this item */
  val title: String =
    front_matter.extractOrElse("title")(
      front_matter.getOrElse("name")(
        titleParser(filepath).getOrElse("untitled" + this.toString)
      )
    ) // so that titles are always different for different items

  // TODO: check with jekyll if it needs more default variables
  lazy val locals =
    val used = List("title")
    val obj = Obj()
    for
      (s, v) <- front_matter.obj
      if !used.contains(s)
    do obj(s) = v
    obj.obj ++= List("title" -> title)
    DObj(obj)

  /** If there's some front\_matter, then the main\_matter will be conerted with
    * appropriate converter. Otherwise, the identity will be returned
    */
  def render: String =
    if front_matter.obj.isEmpty then main_matter
    else Converters.convert(main_matter, filepath)

/** Defines the collection of generic item */
class GenericCollection(val name: String) extends Collection[Item]:

  def apply(directory: String, globals: DObj) =
    val files = getListOfFilepaths(directory)
    def f(fn: String) =
      val item = new GenericItem(directory, fn, globals)
      (item.title, item)
    items = files.map(f).toMap
