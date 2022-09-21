package com.anglypascal.scalite.trees

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.typesafe.scalalogging.Logger

trait AnyStylePost extends TreeStyle[PostLike]

object CategoryStylePost extends CategoryStyle[PostLike]

object TagStylePost extends TagStyle[PostLike]

object PostForests extends Forest[PostLike]:

  protected override val logger = Logger("PostForest")

  val sectionName: String = "forests"

  addTreeStyle(TagStylePost)
  addTreeStyle(CategoryStylePost)

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
    addTreeStyle(TagStylePost)
    addTreeStyle(CategoryStylePost)
