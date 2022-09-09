package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.collections.compareBy
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.*
import com.anglypascal.scalite.utils.cmpOpt
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.plugins.GroupHooks

/** Group implementation for PostLike objects. PostGroup is a Page, so may be
  * rendered to a webpage.
  */
class PostGroup(val groupType: String, val groupName: String)(
    protected val configs: MObj,
    protected val globals: IObj
) extends Group[PostLike]
    with Page:

  protected override val logger = Logger("PostGroup")

  /** Each individual group object can be finetuned by adding a section in the
    * Defaults with scope = group name and type = group type.
    */
  private val scopedDefaults = ScopedDefaults.getDefaults(groupName, groupType)

  lazy val identifier = permalink

  private def getVal(key: String)(default: String): String =
    extractChain(scopedDefaults, configs)(key)(default)

  protected lazy val outputExt: String =
    getVal("outputExt")(Defaults.PostsGroup.outputExt)

  protected lazy val sortBy: String =
    getVal("sortBy")(Defaults.PostsGroup.sortBy)

  lazy val permalink: String =
    val permalinkTemplate: String =
      getVal("baseLink")(Defaults.PostsGroup.baseLink) +
        getVal("relativeLink")(Defaults.PostsGroup.relativeLink)

    val urlObj = MObj(
      "name" -> groupName,
      "type" -> groupType
    )
    urlObj.update(configs)
    purifyUrl(URL(permalinkTemplate)(IObj(urlObj)))

  /** Add a new post to this collection */
  def addPost(post: PostLike) =
    add(post.title, post)
    post.addGroup(groupType)(this)
    logger.trace(s"adding $post")

  /** Name of the layout to be used for rendering the page for this PostsGroup.
    * If not specified in the global settings, this defaults back to "gType"
    */
  protected val layoutName =
    getVal("layout")(globals.getOrElse(groupType + "Layout")(groupType))

  /** Convert the given post to a weeJson obj that will be used to render this
    * post's representative in the page of this PostsGroup. Is intended for
    * overriding.
    *
    * @param post
    *   The post to be converted
    * @return
    *   weeJson obj, with the required mappings for the rendering
    */
  protected def postToItem(post: PostLike): IObj =
    post.locals match
      case a: IObj => a
      case null    => IObj()

  /** The local varibales that will be used to render the PostsGroup page. */
  lazy val locals: IObj =
    val obj = MObj(
      "title" -> groupName,
      "url" -> permalink
    )
    obj update configs
    GroupHooks.beforeLocals
      .foldLeft(obj)((o, h) => o update h(globals)(IObj(o)))
    IObj(obj)

  /** Should the tag be rendered in a separate page? */
  val visible =
    scopedDefaults.extractOrElse("visible")(
      configs.extractOrElse("visible")(true)
    )

  /** Return the rendered html string of this page */
  protected lazy val render: String =
    val c = MObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(items.map(postToItem).toList)
    )
    val con = GroupHooks.beforeRenders
      .foldLeft(c)((o, h) => h(globals)(IObj(o)))
    val ren = layout match
      case Some(l) =>
        logger.trace(s"writing $this to $permalink")
        l.renderWrap(IObj(con))
      case None =>
        logger.warn(s"no layout found for $groupType ${ERROR(groupName)}")
        ""
    GroupHooks.afterRenders.foldLeft(ren)((s, h) => h(globals)(locals, s))

  private def compare(fst: PostLike, snd: PostLike): Boolean =
    compareBy(fst, snd, sortBy) < 0

  def process(dryRun: Boolean = false): Unit =
    write(dryRun)
    GroupHooks.afterWrites foreach { _.apply(globals)(this) }
