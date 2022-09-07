package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.*
import com.typesafe.scalalogging.Logger

/** Defines SuperGroup for PostLike objects */
abstract class PostSuperGroup(
    val groupType: String,
    protected val configs: MObj,
    protected val globals: IObj
) extends SuperGroup[PostLike]
    with Page:

  protected override val logger = Logger("PostSuperGroup")

  val groupName: String = groupType

  protected lazy val _configs =
    configs.copy update ScopedDefaults.getDefaults("", groupType)

  lazy val identifier: String = permalink

  lazy val permalink: String =
    val permalinkTemplate: String =
      _configs.extractOrElse("baseLink")(Defaults.PostsGroup.baseLink)

    val urlObj = MObj("type" -> groupType)
    urlObj update _configs

    purifyUrl(URL(permalinkTemplate)(IObj(urlObj)))

  protected lazy val outputExt: String =
    _configs.extractOrElse("outputExt")(Defaults.PostsGroup.outputExt)

  /** Should the tag be rendered in a separate page? */
  val visible = _configs.extractOrElse("visible")(true)

  /** Return the rendered html string of this page */
  protected lazy val render: String =
    val context = IObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(items.map(_.locals).toList)
    )
    layout match
      case Some(l) =>
        logger.trace(s"writing $groupType to $permalink")
        l.renderWrap(context)
      case None =>
        logger.warn(s"${ERROR(groupName)}[$groupType] has no layout")
        ""

  protected val layoutName: String = _configs.extractOrElse("layout")(groupType)

  protected[groups] def process(dryRun: Boolean = false): Unit =
    items foreach { _.process(dryRun) }
