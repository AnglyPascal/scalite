package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.{Page, Post, Layout}
import com.anglypascal.scalite.NoLayoutException

import com.rallyhealth.weejson.v1.{Obj, Arr}
import scala.collection.mutable.Set

/** Each CollectionOfPosts object represents a collection that posts can belong
  * to. Tag and Categories are the two sublcasses of this trait. A collection of
  * posts contains a list of posts with this ctype. It can be rendered into a
  * new page with a list of all the post with this ctype.
  *
  * @param name
  *   collection type name name
  */
trait PostsBag(
    val ctype: String,
    val name: String,
    globals: Obj
) extends Bag[Post]
    with Page:

  /** TODO: Maybe we WILL need globals sent here as well, as we do want the user
    * to be able to specify some stuff like url, or tag name and such
    */

  /** Name layout to be used for rendering the Tag page. If not specified in the
    * global settings, this defaults back to "ctype"
    */
  val parent_name = ctype

  val things = Set[Post]()

  def addItem(post: Post) = things.add(post)

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

  def render(partials: Map[String, Layout]): String =
    val context = Obj(
      "site" -> globals,
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

/** TODO: Give a default generator that generates a PostsBag once given some
  * values in the globals
  */
