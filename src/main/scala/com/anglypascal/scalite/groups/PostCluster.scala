package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.typesafe.scalalogging.Logger

/** Handles Cluster of PostLike objects. The default implementation of Cluster,
  * so looks out for the "groups" section in the configuration.
  */
object PostCluster extends Cluster[PostLike]:

  protected override val logger = Logger("PostCluster")

  val sectionName: String = "groups"

  addGroupStyle(TagStyle)
  addGroupStyle(CatStyle)

  protected lazy val defaultConfig: MObj =
    import Defaults.Group
    import Defaults.Tags
    import Defaults.Categories
    MObj(
      "tags" -> MObj(
        "title" -> Tags.title,
        "gType" -> Tags.gType,
        "sortBy" -> Tags.sortBy,
        "baseLink" -> Tags.baseLink,
        "relativeLink" -> Tags.relativeLink,
        "separator" -> Tags.separator,
        "style" -> Tags.style
      ),
      "categories" -> MObj(
        "title" -> Categories.title,
        "gType" -> Categories.gType,
        "sortBy" -> Categories.sortBy,
        "baseLink" -> Categories.baseLink,
        "relativeLink" -> Categories.relativeLink,
        "separator" -> Categories.separator,
        "style" -> Categories.style
      )
    )

  /** Called by a PostLike to add itself to all available SuperGroups */
  def addToGroups(post: PostLike): Unit =
    for (_, groupObj) <- superGroups do groupObj.addItem(post.title, post)
