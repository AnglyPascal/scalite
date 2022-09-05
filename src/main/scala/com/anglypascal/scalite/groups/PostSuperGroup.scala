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

abstract class PostSuperGroup(
    val groupType: String,
    configs: MObj,
    globals: IObj
) extends SuperGroup[PostLike]
    with Page:

  private val logger = Logger("PostSuperGroup")

  private val _configs =
    configs.copy update ScopedDefaults.getDefaults("", groupType)

  lazy val identifier: String = permalink

  val groupName: String = groupType

  lazy val permalink: String =
    val permalinkTemplate: String =
      _configs.getOrElse("baseLink")(Defaults.PostsGroup.baseLink) +
        _configs.getOrElse("relativeLink")(Defaults.PostsGroup.relativeLink)

    val urlObj = MObj("type" -> groupType)
    urlObj.update(_configs)

    purifyUrl(URL(permalinkTemplate)(IObj(urlObj)))

  protected lazy val outputExt: String =
    _configs.getOrElse("outputExt")(Defaults.PostsGroup.outputExt)

  /** Should the tag be rendered in a separate page? */
  val visible =
    _configs.getOrElse("visible")(true)

  /** Return the rendered html string of this page */
  protected lazy val render: String =
    val context = IObj(
      "site" -> globals,
      "page" -> locals,
      "items" -> DArr(items.map(_.locals).toList)
    )
    layout match
      case Some(l) =>
        logger.trace(s"writing $this to $permalink")
        l.render(context)
      case None =>
        logger.warn(s"no layout found for $groupType ${ERROR(groupName)}")
        ""

  protected val layoutName: String =
    _configs.getOrElse("layout")(groupType)

  def process(dryRun: Boolean = false): Unit =
    items foreach { _.process(dryRun) }
