package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.DataExtensions.*
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MMap}
import com.anglypascal.scalite.Configurable

/** Object that holds all the Groups defined for this website. By default these
  * are Tag and Category. New groups can be added by creating a object of the
  * trait Group.
  */
object Groups extends Configurable:

  val sectionName: String = "groups"

  /** The available style defintiions */
  private val styles = LinkedHashMap[String, GroupConstructor](
    "tag" -> TagStyle,
    "category" -> CatStyle
  )

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

  def addNewGroupStyle(style: GroupConstructor) =
    styles += style.styleName -> style

  /** Set of the available Groups for this site */
  private val availableGroups: ListBuffer[GroupType] = ListBuffer()

  /** Add a new Group to this site */
  def addNewGroup(group: GroupType) = availableGroups += group

  /** Create the groups indicated by the global settings. */
  def apply(configs: MObj, globals: IObj): Unit =
    groupsConfig.update(configs)
    for (key, value) <- groupsConfig do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")("tags")
          val gType = value.extractOrElse("gType")("tags")
          val grpStyle = styles(style)(gType, value, globals)
          addNewGroup(new GroupType(grpStyle, globals))
        case _ => ()

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(): Unit = ???

  def addToGroups(post: PostLike): Unit =
    for groupObj <- availableGroups do groupObj.addToGroups(post)
