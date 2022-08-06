package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.{Page, Post, Layout}
import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.utils.{getOrElse, prettify}

import com.rallyhealth.weejson.v1.{Obj, Arr}
import scala.collection.mutable.Set

/** Each PostsBag object represents a collection that posts can belong to. Tag
  * and Categories are the two pre-defined sublcasses of this trait.
  *
  * @param ctype
  *   the type of this PostsBag, like "tag" or "category"
  * @param name
  *   the name of this PostsBag
  * @param globals
  *   a weejson obj containing the global options for this site
  */
trait PostsBag(
    val ctype: String,
    val name: String,
    globals: Obj
) extends Page:

  /** Set of posts that belong to this collection. */
  val posts: Set[Post] = Set()

  /** Add a new post to this collection */
  def addPost(post: Post) = posts += post

  /** Name of the layout to be used for rendering the page for this PostsBag. If
    * not specified in the global settings, this defaults back to "ctype"
    */
  val parent_name = globals.getOrElse(ctype + "_layout")(ctype)

  /** Convert the given post to a weeJson obj that will be used to render this
    * post's representative in the page of this PostsBag. Is intended for
    * overriding.
    *
    * @param post
    *   The post to be converted
    * @return
    *   weeJson obj, with the required mappings for the rendering
    */
  protected def postToItem(post: Post): Obj =
    Obj(
      "post_title" -> post.title,
      "post_date" -> post.date,
      "post_url" -> post.url,
      "post_excerpt" -> post.excerpt
    )

  /** The local varibales that will be used to render the PostsBag page. */
  private val locals: Obj =
    Obj(
      "title" -> prettify(name)
    )

  /** Render the page of this PostsBag.
    *
    * @param partials
    *   the global partials sent by the caller to be used in Mustache rendering
    * @return
    *   the rendered page string
    */
  def render(partials: Map[String, Layout]): String =
    val context = Obj(
      "site" -> globals,
      "page" -> locals,
      "items" -> Arr(posts.toList.map(postToItem))
    )
    parent match
      case Some(l) =>
        l.render(context, partials)
      case None =>
        throw NoLayoutException(s"No layout found for $ctype collections")

  /** needs its special url
    */

/** TODO: Give a default generator that generates a PostsBag once given some
  * values in the globals
  */
