package com.anglypascal.scalite.groups

/** Provides classes for dealing with with Groups of posts. By default the
  * subclasses Tag and Category are defined.
  */

import com.anglypascal.scalite.collections.Post

import scala.collection.mutable.{LinkedHashMap, Set}
import com.rallyhealth.weejson.v1.Obj
import com.anglypascal.scalite.utils.DObj

/** Creates a new type of Group. Needs the implementation of addToGroups which
  * defines how a post is added to the Group.
  *
  * To define a new Group with custom behavior, extend this trait by creating a
  * GroupType class/object that can override behavior of PostsGroup. The addToGroups
  * method defines the way posts adds themselves to this Group.
  */
trait Group(ctype: String):

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
      extends PostsGroup(ctype, name, globals)

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
  def addToGroups(post: Post, globals: DObj): Unit

  // add this Group to Group.availableGroups
  Group.addNewGroup(this)

/** Companion object that holds all the Groups defined for this website. By
  * default these are Tag and Category. New groups can be added by creating a
  * object of the trait Group.
  */
object Group:

  /** Set of the available Groups for this site */
  val availableGroups: Set[Group] = Set()

  /** Add a new Group to this site */
  def addNewGroup(group: Group) = availableGroups += group
