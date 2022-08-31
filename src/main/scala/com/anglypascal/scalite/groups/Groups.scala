package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DataExtensions.*
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MMap}

/** Object that holds all the Groups defined for this website. By default these
  * are Tag and Category. New groups can be added by creating a object of the
  * trait Group.
  */
object Groups:

  /** The available style defintiions */
  private val styles = LinkedHashMap[String, GroupConstructor](
    "tag" -> TagStyle,
    "category" -> CatStyle
  )

  def addNewGroupStyle(style: GroupConstructor) =
    styles += style.styleName -> style

  /** Set of the available Groups for this site */
  private val availableGroups: ListBuffer[GroupType] = ListBuffer()

  /** Add a new Group to this site */
  def addNewGroup(group: GroupType) = availableGroups += group

  /** Create the groups indicated by the global settings. */
  def apply(grpObj: MMap[String, Obj], globals: DObj): Unit =
    for (key, value) <- grpObj do
      val style = value.extractOrElse("style")("tags")
      val gType = value.extractOrElse("gType")("tags")
      val grpStyle = styles(style)(gType, value, globals)
      addNewGroup(new GroupType(grpStyle, globals))

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(): Unit = ???

  def addToGroups(post: PostLike): Unit =
    for groupObj <- availableGroups do groupObj.addToGroups(post)
