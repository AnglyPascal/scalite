package com.anglypascal.scalite.groups

/** Provides classes for dealing with with Groups of posts. By default the
  * subclasses Tag and Category are defined.
  */

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer

/** Creates a new type of Group. Needs the implementation of addToGroups which
  * defines how a post is added to the Group.
  *
  * To define a new Group with custom behavior, extend this trait by creating a
  * GroupType class/object that can override behavior of PostsGroup. The
  * addToGroups method defines the way posts adds themselves to this Group.
  */
trait Group(val ctype: String):

  /** Underlying PostsGroup class which will take care of generating individual
    * page for the groups of this type.
    *
    * @constructor
    *   Create a new element of this group type
    * @param name
    *   name of this element
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  abstract class GroupType(name: String, globals: DObj)
      extends PostsGroup(ctype, name, globals):
    override def toString(): String = s"$name"

  /** Defines how posts add themselves to this group type. Usually it's by a
    * combination of specifying group names in the front matter as a string or
    * list, or by indiciating group names in the filename or inside a class
    * variable.
    *
    * @param post
    *   A post object to be added to the groups of this type
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  def addToGroups(post: PostLike, globals: DObj): Unit

/** Object that holds all the Groups defined for this website. By default these
  * are Tag and Category. New groups can be added by creating a object of the
  * trait Group.
  */
object Groups:

  /** Set of the available Groups for this site */
  private val _availableGroups: ListBuffer[Group] = ListBuffer()
  def availableGroups = _availableGroups.toList

  /** Add a new Group to this site */
  def addNewGroup(group: Group) = _availableGroups += group

  /** TODO: Add global configs, which options do we need to add? Groups can
    * potentially be more powerful I guess
    */
  def apply(grpObj: Obj): Unit = ???

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(): Unit = ???
