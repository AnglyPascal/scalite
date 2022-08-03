package com.anglypascal.scalite.documents

import com.anglypascal.scalite.converters.convert
import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.collections.*
import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.URL

import com.rallyhealth.weejson.v1.{Obj, Str, Bool}
import scala.collection.mutable.LinkedHashMap

/** Reads the content of a post file and prepares a Page object for that.
  *
  * @param filename
  *   path to the post file
  *
  * TODO: also what about posts inside a collection? those will have a slightly
  * different set locals, no?
  *
  * TODO: add lists of tags and categories. Ok this is where collection comes
  * into play. Give functions that lets us add a tag to this post.
  */
class Post(filename: String, layouts: Map[String, Layout], globals: Obj)
    extends Document(filename, globals)
    with Ordered[Post]:

  if parent_name == "" then
    throw NoLayoutException("No layout specified in the front matter")

  setupParent(layouts)

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filename. If the filename has no title given, simply
    * name this post "untitled"
    */
  def title: String = _title
  private val _title: String =
    front_matter.getOrElse("title")(
      titleParser(filename).getOrElse("untitled")
    )

  /** TODO: Here it should also check modified time, but that'd have to be
    * checked by reading the file >.>
    */
  private val dataObj: Obj =
    val dateString = front_matter.getOrElse("date")(filename)
    val dateFormat = front_matter.getOrElse("date_format")(
      globals.getOrElse("date_format")("yyyy-MM-dd")
    )
    val obj = dateParseObj(dateString, dateFormat)
    obj("title") = title

    obj

  /** the date in front_matter have more information. like time and timezone.
    * Nothing is necessary, but if date is being given, it has to be given in
    * full, if time is given, it has to be given in full.
    */
  def date: String = _date
  private val _date = dataObj.getOrElse("date_string")("undated")

  def tagNames: List[String] = _tagNames
  private val _tagNames =
    if front_matter.obj.contains("tags") then
      front_matter("tags") match
        case s: Str => List(s.str)
        case a: Arr => a.arr.toList.map(_.str)
        case _      => List()
    else List()

  private val _tags: LinkedHashMap[String, Tag] = LinkedHashMap()
  def addTag(name: String, tag: Tag) = _tags(name) = tag
  def tags: LinkedHashMap[String, Tag] = _tags

  /** Permalink of post TODO: more doc and check later
    */
  def permalink: String = _permalink
  private val _permalink =
    front_matter.getOrElse("permalink")(
      globals.getOrElse("permalink")(filename)
    )

  val url: String = URL("template goes here")(front_matter)

  Obj(
    "title" -> title,
    "date" -> date
    /** also append all the other tags in front_matter
      */
  )

  /** TODO: Ideally this needs to be handled before rendering is done. So this
    * suggests we render stuff right before writing them to the disk
    */
  val visible: Boolean = front_matter.getOrElse("visible")(false)

  /** Convert the contents of the post to HTML, throwing an exception on failure
    * to do so
    *
    * TODO: there can be different types of exceptions
    */
  def render(partials: Map[String, Layout]): String =
    val locals = setupLocals(globals)

    val str = convert(main_matter, filename) match
      case Right(s) => s
      case Left(e)  => throw e
    val context = Obj("site" -> globals, "post" -> locals, "content" -> str)
    parent match
      case l: Layout =>
        l.render(context, partials)
      case null => str

  // def addToCollections: Unit = ???

  /** TODO: if show_excerpt is true, then create an excerpt object here? and add
    * the excerpt to the obj
    */

  /** TODO: Related posts? Custom sorting?
    */
  def compare(that: Post) = this.date compare that.date

  /** TODO: need to add support for "visible"
    */

object Post:
  def apply(
      filename: String,
      layouts: Map[String, Layout],
      globals: Obj
  ): Post =
    new Post(filename, layouts, globals)
