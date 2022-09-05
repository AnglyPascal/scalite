package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.Configurable

/** Object in charge of creating and configuring PostGroups
  *
  * Is a Configurable, so the configuration can be added under the "groups"
  * section of \_config.yml
  */
object PostGroups extends Groups[PostLike]:

  val sectionName: String = "groups"

  addNewGroupStyle(TagStyle)
  addNewGroupStyle(CatStyle)

  protected lazy val groupsConfig: MObj =
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

  /** Called by a PostLike to add itself to all the available groups */
  def addToGroups(post: PostLike): Unit =
    for (_, groupObj) <- groupTypes do groupObj.add(post)

  override def toString(): String =
    groupTypes.map(_._2.toString).mkString("\n")
