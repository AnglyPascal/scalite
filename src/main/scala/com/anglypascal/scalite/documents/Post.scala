package com.anglypascal.scalite.documents

import com.anglypascal.scalite.converters.convert
import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.converters.hasConverter
import com.anglypascal.scalite.bags.{PostsBag, BagHandler}
import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.URL

import com.rallyhealth.weejson.v1.{Obj, Str, Arr, Bool}
import scala.collection.mutable.{LinkedHashMap, Set}
import com.rallyhealth.weejson.v1.Value

/** Reads the content of a post file and prepares a Page object for that.
  *
  * @param filename
  *   "path" to the post file
  *
  * TODO: Check that it's the path that's being pushed in, and not just the
  * filename. The whole path is important as other than the _post suffix, all
  * the other subfolders will be counted as categories for this post
  *
  * TODO: Need to factor out layout form the pages as well. It has a
  * fundamentally different render function, it needs to be anchored to by posts
  * almost like tags, and there will be many of them, so treat it similarly to
  * tags. This will also allow for a different layout implementation :0
  */
class Post(val filename: String, globals: Obj)
    extends Reader(filename)
    with Page
    with Ordered[Post]:

  /** Get the parent layout name, if it exists. Layouts might not have a parent
    * layout, but each post needs to have one.
    *
    * TODO: In the Posts class, check if layout is empty, and throw exception
    */
  val parent_name =
    if front_matter.obj.contains("layout") then
      front_matter("layout") match
        case s: Str => s.str
        case _      => "post"
    else "post"

  /** Search for the parent layout in the map holding layouts.
    *
    * TODO: Make this one abstract as well
    */
  Layout.layouts.get(parent_name) match
    case Some(l) => _parent = l
    case _       => _parent = null

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
    val dateFormat =
      front_matter.getOrElse("date_format")(
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

  /** Return the global settings for the collection-type ctype
    */
  def getBagsList(ctype: String): Value =
    if front_matter.obj.contains(ctype) then front_matter(ctype)
    else null

  /** Adds the collection in the set of this collection-type
    */
  def addBag[A <: PostsBag](ctype: String)(a: A): Unit =
    if bags.contains(ctype) then bags(ctype) += a
    else bags += ctype -> Set(a)

  /** The map holding sets of collection-types
    */
  private val bags: LinkedHashMap[String, Set[PostsBag]] =
    LinkedHashMap()

  /** Processes the collections this post belongs to, for the collections
    * specified in the list in CollectionsHandler companion object
    */
  def processBags(): Unit =
    for bagObj <- BagHandler.availableBags do bagObj.addToBags(this, globals)

  /** Permalink of post TODO: more doc and check later
    */
  def permalink: String = _permalink
  private val _permalink =
    front_matter.getOrElse("permalink")(
      globals.getOrElse("permalink")(filename)
    )

  def url: String = _url
  private val _url = URL("template goes here")(front_matter)

  private val locals = Obj(
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
    val str = convert(main_matter, filename) match
      case Right(s) => s
      case Left(e)  => throw e
    val context = Obj("site" -> globals, "post" -> locals, "content" -> str)
    parent match
      case l: Layout =>
        l.render(context, partials)
      case null => str

  /** TODO: if show_excerpt is true, then create an excerpt object here? and add
    * the excerpt to the obj
    */

  /** TODO: Related posts? Custom sorting?
    */
  def compare(that: Post) = this.date compare that.date


/** Companion Object
  */
object Post:

  def posts = _posts
  private var _posts: Map[String, Post] = _

  def apply(directory: String, globals: Obj): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processBags()
      (post.title, post)
    _posts = files.filter(hasConverter).map(f).toMap

    posts
