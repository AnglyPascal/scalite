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
import com.anglypascal.scalite.utils.cmpOpt
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL

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
class PostsGroup(val ctype: String, configs: Obj)(
    val name: String,
    globals: DObj
) extends Page:

  private val logger = Logger(s"$ctype: $name")

  /** Set of posts that belong to this collection. */
  private val _posts: ArrayBuffer[PostLike] = ArrayBuffer()
  def posts = _posts.sortWith(compare)

  lazy val filepath = permalink

  protected lazy val outputExt: String =
    configs.extractOrElse("outputExt")(Defaults.PostsGroup.outputExt)

  protected lazy val sortBy: String =
    configs.extractOrElse("sortBy")(Defaults.PostsGroup.sortBy)

  lazy val permalink: String =
    val permalinkTemplate: String =
      configs.extractOrElse("permalink")(Defaults.PostsGroup.permalink)
    val urlObj = Obj(
      "name" -> name,
      "ctype" -> ctype
    )
    for (k, v) <- configs.obj do urlObj(k) = v
    purifyUrl(URL(permalinkTemplate)(DObj(urlObj)))

  /** Add a new post to this collection */
  def addPost(post: PostLike) =
    _posts += post
    post.addGroup(ctype)(this)

  /** Name of the layout to be used for rendering the page for this PostsGroup.
    * If not specified in the global settings, this defaults back to "ctype"
    */
  protected val layoutName =
    configs.getOrElse("layout")(
      globals.getOrElse(ctype + "Layout")(ctype)
    )

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
  lazy val locals: DObj =
    val obj = Obj(
      "title" -> name,
      "url" -> permalink
    )
    for (k, v) <- configs.obj do obj(k) = v
    DObj(obj)

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
      "items" -> DArr(posts.map(postToItem).toList)
    )
    layout match
      case Some(l) =>
        l.render(context)
      case None =>
        logger.warn(s"no layout found for $ctype ${ERROR(name)}")
        ""

  private def compareBy(fst: PostLike, snd: PostLike, key: String): Int =
    val s = cmpOpt(fst.locals.get(key), fst.locals.get(key))
    if s != 0 then return s
    val k = Defaults.PostsGroup.sortBy
    val n = cmpOpt(fst.locals.get(k), fst.locals.get(k))
    if n != 0 then return n
    0

  private def compare(fst: PostLike, snd: PostLike): Boolean =
    compareBy(fst, snd, sortBy) < 0
