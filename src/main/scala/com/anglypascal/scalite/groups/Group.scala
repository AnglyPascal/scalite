package com.anglypascal.scalite.groups

/** Provides classes for dealing with with Groups of posts. By default the
  * subclasses Tag and Category are defined.
  */

import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.rallyhealth.weejson.v1.Obj

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.plugins.Plugin

/** Creates a new type of Group. Needs the implementation of addToGroups which
  * defines how a post is added to the Group.
  *
  * To define a new Group with custom behavior, extend this trait by creating a
  * GroupType class/object that can override behavior of PostsGroup. The
  * addToGroups method defines the way posts adds themselves to this Group.
  */
class GroupType(style: GroupStyle, globals: DObj):

  protected val groups = LinkedHashMap[String, PostsGroup]()

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
  def addToGroups(post: PostLike, globals: DObj): Unit =
    // names of categories this post belongs to
    val catNames = style.getGroupNames(post)
    // for each category, add this post to it and add this category back to the post
    for cat <- catNames do
      groups.get(cat) match
        case Some(t) =>
          t.addPost(post)
        case None =>
          groups(cat) = style.groupConstructor(cat, globals)
          groups(cat).addPost(post)

trait GroupStyle extends Plugin:
  /** */
  def groupConstructor(name: String, globals: DObj): PostsGroup

  /** Process the names of the tags this post belongs to by examining it's tags
    * front matter entry. It also slugifies the category names to make it
    * universal.
    *
    * @param post
    *   a post to be added to the tags
    * @return
    *   an iterator with all the names of the tags
    */
  def getGroupNames(post: PostLike): Iterable[String]

trait GroupConstructor:
  def apply(cType: String, configs: Obj): GroupStyle
