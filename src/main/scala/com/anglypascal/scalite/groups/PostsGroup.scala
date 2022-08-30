package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.StringProcessors.*
import com.anglypascal.scalite.utils.Colors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.Set
import com.typesafe.scalalogging.Logger

/** Each PostsGroup object represents a collection that posts can belong to. Tag
  * and Categories are the two pre-defined sublcasses of this trait.
  *
  * @param ctype
  *   the type of this PostsGroup, like "tags" or "categories"
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

  private val logger = Logger("PostsGroup")

  /** Set of posts that belong to this collection. */
  private val _posts: Set[PostLike] = Set()
  def posts = _posts.toList

  lazy val filepath = s"/groups/$ctype/$name"

  /** Add a new post to this collection */
  def addPost(post: PostLike) = _posts += post

  /** Name of the layout to be used for rendering the page for this PostsGroup.
    * If not specified in the global settings, this defaults back to "ctype"
    */
  protected val layoutName = globals.getOrElse(ctype + "Layout")(ctype)

  /** Convert the given post to a weeJson obj that will be used to render this
    * post's representative in the page of this PostsGroup. Is intended for
    * overriding.
    *
    * @param post
    *   The post to be converted
    * @return
    *   weeJson obj, with the required mappings for the rendering
    */
  protected def postToItem(post: PostLike): DObj =
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
    layout match
      case Some(l) =>
        l.render(context)
      case None =>
        logger.warn(s"no layout found for $ctype ${ERROR(name)}")
        ""

  /** needs its special url
    */

/** TODO: Give a default generator that generates a PostsGroup once given some
  * values in the globals
  */
