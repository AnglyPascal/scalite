package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.collections.compareBy
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.*
import com.anglypascal.scalite.utils.cmpOpt
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer

/** Each PostsGroup object represents a generic group containing posts. Each
  * PostsGroup has a gType and a name, for example, a tag group might have gType
  * "tags" and a name "coding". A PostsGroup is customizable through the configs
  * Obj passed at the construction.
  *
  * @param gType
  *   the type of this PostsGroup, like "tags" or "categories"
  * @param configs
  *   a weejson obj containing the configuration options for this PostsGroup
  * @param name
  *   the name of this PostsGroup
  * @param globals
  *   a weejson obj containing the global options for this site
  */
class PostsGroup(
    val gType: String,
    private val configs: Obj,
    private val globals: DObj
)(val name: String)
    extends Page:

  private val logger = Logger(toString)

  /** Each individual group object can be finetuned by adding a section in the
    * Defaults with scope = group name and type = group type.
    */
  private val scopedDefaults = ScopedDefaults.getDefaults(name, gType)

  /** Set of posts that belong to this collection. */
  protected val _posts: ArrayBuffer[PostLike] = ArrayBuffer()
  def posts = _posts.sortWith(compare)

  lazy val identifier = permalink

  protected lazy val outputExt: String =
    scopedDefaults.extractOrElse("outputExt")(
      configs.extractOrElse("outputExt")(Defaults.PostsGroup.outputExt)
    )

  protected lazy val sortBy: String =
    scopedDefaults.extractOrElse("sortBy")(
      configs.extractOrElse("sortBy")(Defaults.PostsGroup.sortBy)
    )

  lazy val permalink: String =
    val permalinkTemplate: String =
      scopedDefaults.extractOrElse("permalink")(
        configs.extractOrElse("permalink")(Defaults.PostsGroup.permalink)
      )
    val urlObj = Obj(
      "name" -> name,
      "gType" -> gType
    )
    for (k, v) <- configs.obj do urlObj(k) = v
    purifyUrl(URL(permalinkTemplate)(DObj(urlObj)))

  /** Add a new post to this collection */
  def addPost(post: PostLike) =
    _posts += post
    post.addGroup(gType)(this)
    logger.trace(s"adding $post")

  /** Name of the layout to be used for rendering the page for this PostsGroup.
    * If not specified in the global settings, this defaults back to "gType"
    */
  protected val layoutName =
    scopedDefaults.extractOrElse("layout")(
      configs.extractOrElse("layout")(
        globals.getOrElse(gType + "Layout")(gType)
      )
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
  val visible =
    scopedDefaults.extractOrElse("visible")(
      configs.extractOrElse("visible")(true)
    )

  /** Return the rendered html string of this page */
  protected lazy val render: String =
    val context = DObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(posts.map(postToItem).toList)
    )
    layout match
      case Some(l) =>
        logger.trace(s"writing $this to $permalink")
        l.render(context)
      case None =>
        logger.warn(s"no layout found for $gType ${ERROR(name)}")
        ""

  private def compare(fst: PostLike, snd: PostLike): Boolean =
    compareBy(fst, snd, sortBy) < 0

  override def toString(): String = s"$gType(${GREEN(name)})"
