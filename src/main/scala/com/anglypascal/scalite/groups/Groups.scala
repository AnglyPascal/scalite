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
object Groups extends Configurable:

  val sectionName: String = "groups"

  /** The available style defintiions */
  private val styles = LinkedHashMap[String, GroupConstructor](
    "tag" -> TagStyle,
    "category" -> CatStyle
  )

  /** Add a new GroupConstructor for a new GroupStyle to this site */
  def addNewGroupStyle(style: GroupConstructor) =
    styles += style.styleName -> style

  private lazy val groupsConfig: MObj =
    import Defaults.Group
    import Defaults.Tags
    import Defaults.Categories
    MObj(
      "tags" -> MObj(
        "title" -> Tags.title,
        "gType" -> Tags.gType,
        "sortBy" -> Tags.sortBy,
        "permalink" -> Tags.permalink,
        "separator" -> Tags.separator,
        "style" -> Tags.style
      ),
      "categories" -> MObj(
        "title" -> Categories.title,
        "gType" -> Categories.gType,
        "sortBy" -> Categories.sortBy,
        "permalink" -> Categories.permalink,
        "separator" -> Categories.separator,
        "style" -> Categories.style
      )
    )

  /** Set of the available Groups for this site */
  private val groupTypes: LinkedHashMap[String, GroupType] = LinkedHashMap()

  /** Add a new Group to this site */
  private def addNewGroup(gType: String, group: GroupType) = 
    groupTypes += gType -> group

  /** Apply the configuration from groups section */
  def apply(configs: MObj, globals: IObj): Unit =
    groupsConfig.update(configs)
    for (key, value) <- groupsConfig do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")("tag")
          val gType = value.extractOrElse("gType")("tags")
          val grpStyle = styles(style)(gType, value, globals)
          addNewGroup(gType, new GroupType(grpStyle, globals))
        case _ => ()

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(dryRun: Boolean = false): Unit = 
    for (_, grp) <- groupTypes do 
      grp.process(dryRun)

  /** Called by a PostLike to add itself to all the available groups */
  def addToGroups(post: PostLike): Unit =
    for (_, groupObj) <- groupTypes do groupObj.addPostToGroups(post)

  override def toString(): String = 
    groupTypes.map(_._2.toString).mkString("\n")

