package com.anglypascal.scalite.trees

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.typesafe.scalalogging.Logger

abstract class PostTree(_configs: MObj) extends Tree[PostLike] with Page:

  protected override val logger = Logger("PostSuperTree")

  protected val configs =
    pathToRootNames.foldLeft(_configs.copy)((o, s) =>
      o update ScopedDefaults.getDefaults(s, treeType)
    )

  lazy val identifier: String = s"/$treeType/$treeName"

  lazy val permalink: String =
    val permalinkTemplate: String =
      configs.extractOrElse("permalink")(Defaults.PostTree.permalink)

    val urlObj = MObj(
      "type" -> treeType,
      "path" -> pathToRootNames.mkString("/"),
      "name" -> treeName,
      "outputExt" -> outputExt
    )
    urlObj update configs

    purifyUrl(URL(permalinkTemplate)(IObj(urlObj)))

  protected lazy val outputExt: String =
    configs.extractOrElse("outputExt")(Defaults.PostTree.outputExt)

  /** Should the tag be rendered in a separate page? */
  val visible = configs.extractOrElse("visible")(true)

  lazy val locals: IObj =
    val temp = MObj(
      "type" -> treeType,
      "url" -> permalink,
      "outputExt" -> outputExt,
      "path" -> pathToRootNames.mkString("/")
    )
    temp update _configs
    IObj(temp)

  /** Return the rendered html string of this page */
  protected lazy val render: String =
    val context = IObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(items.map(_.locals).toList),
      "children" -> DArr(children.map(_.locals).toList)
    )
    layout match
      case Some(l) =>
        logger.trace(s"writing $treeType to $permalink")
        l.renderWrap(context)
      case None =>
        logger.warn(s"${ERROR(treeName)}[$treeType] has no layout")
        ""

  protected lazy val layoutName: String =
    configs.extractOrElse("layout")(treeType)

  protected[trees] def process(dryRun: Boolean = false): Unit =
    write(dryRun)
    children foreach { _.process(dryRun) }

object PostForests extends Forest[PostLike]:

  protected override val logger = Logger("PostForest")

  val sectionName: String = "forests"

  addTreeStyle(TagStyle)
  addTreeStyle(CategoryStyle)

  protected def defaultConfig: MObj =
    import Defaults.Tree
    import Defaults.Tags
    import Defaults.Categories
    MObj(
      "tags" -> MObj(
        "title" -> Tags.title,
        "type" -> Tags.tType,
        "sortBy" -> Tags.sortBy,
        "baseLink" -> Tags.baseLink,
        "relativeLink" -> Tags.relativeLink,
        "separator" -> Tags.separator,
        "style" -> Tags.style
      ),
      "categories" -> MObj(
        "title" -> Categories.title,
        "type" -> Categories.tType,
        "sortBy" -> Categories.sortBy,
        "baseLink" -> Categories.baseLink,
        "relativeLink" -> Categories.relativeLink,
        "separator" -> Categories.separator,
        "style" -> Categories.style
      )
    )

  /** Called by a PostLike to add itself to all available SuperGroups */
  def addToForests(post: PostLike): Unit =
    for tree <- trees do tree.addItem(post.title, post)

  override def reset(): Unit =
    super.reset()
    addTreeStyle(TagStyle)
    addTreeStyle(CategoryStyle)
