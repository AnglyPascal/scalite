package com.anglypascal.scalite.groups

import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.collections.Post
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.Set

/** Each PostsGroup object represents a collection that posts can belong to. Tag
  * and Categories are the two pre-defined sublcasses of this trait.
  *
  * @param ctype
  *   the type of this PostsGroup, like "tag" or "category"
  * @param name
  *   the name of this PostsGroup
  * @param globals
  *   a weejson obj containing the global options for this site
  */
trait PostsGroup(
    val ctype: String,
    val name: String,
    globals: DObj
) extends Page:

  /** Set of posts that belong to this collection. */
  private val _posts: Set[Post] = Set()
  def posts = _posts.toList

  /** Add a new post to this collection */
  def addPost(post: Post) = _posts += post

  /** Name of the layout to be used for rendering the page for this PostsGroup.
    * If not specified in the global settings, this defaults back to "ctype"
    */
  protected val parentName = globals.getOrElse(ctype + "Layout")(ctype)

  /** Convert the given post to a weeJson obj that will be used to render this
    * post's representative in the page of this PostsGroup. Is intended for
    * overriding.
    *
    * @param post
    *   The post to be converted
    * @return
    *   weeJson obj, with the required mappings for the rendering
    */
  protected def postToItem(post: Post): DObj =
    post.locals match
      case a: DObj => a
      case null    => DObj()

  /** The local varibales that will be used to render the PostsGroup page. */
  val locals: DObj = DObj(
    "title" -> DStr(name)
  )

  /** Should the tag be rendered in a separate page? */
  lazy val visible = true

  /** Render the page of this PostsGroup.
    *
    * @param partials
    *   the global partials sent by the caller to be used in Mustache rendering
    * @return
    *   the rendered page string
    */
  protected lazy val render: String =
    val context = DObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(posts.toList.map(postToItem))
    )
    parent match
      case Some(paren) =>
        paren.render(context)
      case None =>
        throw NoLayoutException(s"No layout found for $ctype collections")

  /** needs its special url
    */

/** TODO: Give a default generator that generates a PostsGroup once given some
  * values in the globals
  */
