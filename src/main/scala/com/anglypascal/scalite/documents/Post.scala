package com.anglypascal.scalite.documents

import com.anglypascal.scalite.converters.convert
import com.anglypascal.scalite.utils.*

import com.rallyhealth.weejson.v1.{Obj, Str, Bool}
import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.URL

/** Reads the content of a post file and prepares a Page object for that.
  *
  * @param filename
  *   path to the post file
  *
  * TODO: make it comparable, so that sorting is possible
  */
class Post(filename: String, layouts: Map[String, Layout])
    extends Document(filename)
    with Ordered[Post]:

  if parent_name == "" then
    throw NoLayoutException("No layout specified in the front matter")

  set_parent(layouts)

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filename.
    */
  val title: String =
    front_matter.getOrElse("title")(
      titleParser(filename).getOrElse("untitled")
    )

  /** TODO: The date has to be more sophisticated. It should be a dict with each
    * individual elements of the DateTime.
    *
    *   - dateString, year, month, date, week etc
    *
    * This extra information will be passed to the url creator, so the question
    * is how do we do it.
    *
    * Here it should also check modified time
    */
  val date: Option[String] =
    front_matter.getOrElse("date")(
      dateParser(filename).getOrElse("undated")
    )

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
  def render(globals: Obj, partials: Map[String, Layout]): String =
    val permalink: String =
      locals.getOrElse("permalink")(
        globals.getOrElse("permalink")(filename)
      )

    val url: String = URL("template goes here")(locals)

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
  def apply(filename: String, layouts: Map[String, Layout]): Post =
    new Post(filename, layouts)
