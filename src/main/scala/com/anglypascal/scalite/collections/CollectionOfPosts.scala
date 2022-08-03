package com.anglypascal.scalite.collections

import com.anglypascal.scalite.documents.{Page, Post, Layout}
import scala.collection.mutable.Set
import com.rallyhealth.weejson.v1.{Obj, Arr}
import com.anglypascal.scalite.NoLayoutException

/** Each CollectionOfPosts object represents a collection that posts can belong
  * to. Tag and Categories are the two sublcasses of this trait. A collection of
  * posts contains a list of posts with this ctype. It can be rendered into a
  * new page with a list of all the post with this ctype.
  *
  * @param name
  *   collection type name name
  */
trait CollectionOfPosts(
    val ctype: String,
    val name: String,
    globals: Obj // TODO: I'll think about it later
) extends Collection[Post]
    with Page:

  /** Name layout to be used for rendering the Tag page. If not specified in the
    * global settings, this defaults back to "ctype"
    */
  val parent_name =
    if globals.obj.contains(ctype + "_layout") then
      globals(ctype + "_layout").str
    else "ctype"

  val things = Set[Post]()

  /** Helper method to convert a post to it's simplified representation. The
    * returned object should have
    *
    * post_title, post_url, post_excerpt if enabled TODO: add these
    */
  private def postToItem(post: Post): Obj =
    Obj("post_title" -> post.title)

  private val locals: Obj = Obj(
    "title" -> name
  )

  def render(site: Obj, partials: Map[String, Layout]): String =
    val context = Obj(
      "site" -> site,
      "page" -> locals,
      "items" -> Arr(things.toList.map(postToItem))
    )
    parent match
      case l: Layout =>
        l.render(context, partials)
      case null =>
        throw NoLayoutException(s"No layout found for $ctype collections")

  /** needs its special url
    */
