package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Globals
import com.anglypascal.scalite.documents.{Page, Post, Layout}
import scala.collection.mutable.Set
import com.rallyhealth.weejson.v1.{Obj, Arr}
import com.anglypascal.scalite.NoLayoutException

/** Each Tag object represents a tag that posts can have. Tag contains a list of
  * posts with this tag. It can be rendered into a new page with a list of all
  * the post with this tag.
  *
  * @param name
  *   tag name
  */
class Tag(val name: String) extends Collection[Post] with Page:

  /** Name layout to be used for rendering the Tag page. If not specified in the
    * global settings, this defaults back to "tag"
    */
  val parent_name =
    if Globals.site.obj.contains("tag_layout") then
      Globals.site("tag_layout").str
    else "tag"

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

  def render(globals: Obj, partials: Map[String, Layout]): String =
    val context = Obj(
      "site" -> globals,
      "page" -> locals,
      "items" -> Arr(things.toList.map(postToItem))
    )
    parent match
      case l: Layout =>
        l.render(context, partials)
      case null =>
        throw NoLayoutException("No layout found for tag collections")
