package com.anglypascal.scalite.groups

import com.anglypascal.scalite.data.DataExtensions.*
import scala.collection.mutable.ListBuffer
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.data.DObj

/** Object that holds all the Groups defined for this website. By default these
  * are Tag and Category. New groups can be added by creating a object of the
  * trait Group.
  */
object Groups:

  val styles = LinkedHashMap[String, GroupConstructor](
    "tags" -> TagStyle,
    "categories" -> CatStyle
  )

  /** Set of the available Groups for this site */
  private val _availableGroups: ListBuffer[GroupType] = ListBuffer()
  def availableGroups = _availableGroups.toList

  /** Add a new Group to this site */
  def addNewGroup(group: GroupType) = _availableGroups += group

  /** TODO: Add global configs, which options do we need to add? Groups can
    * potentially be more powerful I guess
    */
  def apply(grpObj: MMap[String, Obj], globals: DObj): Unit = 
    for (key, value) <- grpObj do 
      val style = value.extractOrElse("style")("tags")
      val gType = value.extractOrElse("gType")("tags")
      val grpStyle = styles(style)(gType, value)
      addNewGroup(new GroupType(grpStyle, globals))

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(): Unit = ???
