package com.anglypascal.scalite.collections

import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.converters.Converter
import com.anglypascal.scalite.utils.*
import com.anglypascal.scalite.groups.{PostsGroup, Group}
import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.URL

import com.rallyhealth.weejson.v1.{Obj, Str, Arr, Bool}
import scala.collection.mutable.{LinkedHashMap}
import com.rallyhealth.weejson.v1.Value

/** Reads the content of a post file and prepares a Post object.
  *
  * In the front matter, the following entries are standard:
  *   - '''title''': Name of the post. If unspecified, the title will be fetched
  *     from the post filename
  *   - '''date''': Date to be shown on the rendered post. If unspecified, the
  *     date will be fetched from the post filename.
  *   - '''date_format''': The date format specific to this post. If
  *     unspecified, the globally specified format will be used. If that's not
  *     specified either, the format US standard format, June 26, 2022, will be
  *     used.
  *   - '''visible''': Boolean value indicating whether this post should be
  *     converted to a file. If unspecified, the global default, false will be
  *     used, which can be modified globally through "_config.yml"
  *   - '''permalink''': A mustache template inside quotations specifying the
  *     url template for this post. For details about the format, TODO
  *   - '''tags''': A space or comma separated string of tags, or a list of
  *     tags.
  *   - '''categories''': A comma separated string of categories, or a list of
  *     them.
  *   - any other tag will be passed in to the renderer directly unless support
  *     through the plugin is added TODO
  *
  * @param filepath
  *   path to the post file
  * @param globals
  *   a weejson object passed through the "_config.yml" file
  */
class Post(filepath: String, globals: Obj)
    extends Reader(filepath)
    with ReaderOps
    with Page
    with Ordered[Post]:

  /** Get the parent layout name, if it exists. Layouts might not have a parent
    * layout, but each post needs to have one.
    */
  val parent_name =
    if front_matter.obj.contains("layout") then
      front_matter("layout") match
        case s: Str => s.str
        case _ =>
          throw NoLayoutException(
            "the specified layout wasn't found"
          ) // better mssg with logger
    else "post" // default layout for posts

  /** Search for the parent layout in Layout.layouts
    *
    * TODO: Make this one abstract as well
    */
  _parent = Layout.layouts.get(parent_name)

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filepath. If the filepath has no title given, simply
    * name this post "untitled"
    */
  val title: String =
    front_matter.getOrElse("title")(
      titleParser(filepath).getOrElse("untitled")
    )

  /** The date in front_matter have more information. like time and timezone.
    * Nothing is necessary, but if date is being given, it has to be given in
    * full, if time is given, it has to be given in full.
    */
  val date = urlObj.getOrElse("date_string")("undated")

  /** TODO: Will later add support for getting the modified time values in the
    * case when the date/time is not specified in either the title name or the
    * front_matter
    *
    * Maybe DraftPost will extend Post overriding this time handling thing
    */
  private val urlObj: Obj =
    val dateString = front_matter.getOrElse("date")(filepath)
    val dateFormat =
      front_matter.getOrElse("date_format")(
        globals.getOrElse("date_format")("yyyy-MM-dd")
      )
    val obj = dateParseObj(dateString, dateFormat)
    obj("title") = title
    obj("modified_time") = lastModifiedTime(dateFormat)
    obj

  /** Template for the permalink of the post */
  private val permalink =
    front_matter.getOrElse("permalink")(
      globals.getOrElse("default_url_template")(filepath)
    )

  val url = URL(permalink)(urlObj)

  private val _locals =
    val used =
      List("title", "date", "layout", "tags", "categories", "permalink")
    val obj = Obj()
    for
      (s, v) <- front_matter.obj
      if !used.contains(s)
    do obj(s) = v
    obj.obj ++= List(
      "title" -> title,
      "date" -> date,
      "url" -> url,
      "excerpt" -> excerpt
    )
    obj

  def locals = _locals.hardCopy

  /** Returns whether to render this post or not. Default is false. */
  val visible: Boolean =
    front_matter.getOrElse("visible")(
      globals.getOrElse("posts_visibility")(false)
    )

  /** Convert the contents of the post to HTML, throwing an exception on failure
    *
    * TODO: need to change behavior when logger is implemented
    */
  def render(partials: Map[String, Layout]): String =
    val str = Converter.convert(main_matter, filepath) match
      case Right(s) => s
      case Left(e)  => throw e
    val context = Obj("site" -> globals, "post" -> _locals, "content" -> str)
    parent match
      case Some(l) =>
        l.render(context, partials)
      case None => str

  /** TODO: if show_excerpt is true, then create an excerpt object here? and add
    * the excerpt to the obj
    */
  def excerpt: String = ???

  /** TODO: Related posts? Custom sorting? */
  def compare(that: Post) = this.date compare that.date

  /** Return the global settings for the collection-type ctype */
  def getGroupsList(ctype: String): Value =
    if front_matter.obj.contains(ctype) then front_matter(ctype)
    else null

  /** Adds the collection in the set of this collection-type */
  def addGroup[A <: PostsGroup](ctype: String)(a: A): Unit =
    if bags.contains(ctype) then bags(ctype) += a
    else bags += ctype -> Set(a)

  /** The map holding sets of collection-types */
  private val bags: LinkedHashMap[String, Set[PostsGroup]] =
    LinkedHashMap()

  /** Processes the collections this post belongs to, for the collections
    * specified in the list in CollectionsHandler companion object
    */
  def processGroups(): Unit =
    for bagObj <- Group.availableGroups do bagObj.addToGroups(this, globals)

/** Companion Object */
object Post extends Collection[Post]:

  def things = _posts
  private var _posts: List[Post] = _

  val name = "posts"

  def apply(directory: String, globals: Obj): List[Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      post
    _posts = files.filter(Converter.hasConverter).map(f)

    things

/** TODO TODO TODO
  *
  * In jekyll posts are also collections. So if one wishes to turn down the
  * posts output, they can do so by collections: posts: output: false.
  *
  * That means, a collection should have similar structure as the posts.
  * Although the others might not be as elaborate as posts. For example what
  * else would you include in a _staff_members collection, other than some basic
  * things like title, url, date etc?
  *
  * Maybe, provide a public api that lets users define exactly how these
  * collections should render
  */
