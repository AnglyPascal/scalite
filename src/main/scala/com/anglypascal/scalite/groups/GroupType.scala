package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page

import scala.collection.mutable.LinkedHashMap

/** Creates a new type of Group. 
  *
  * @param style
  *   A GroupStyle object defining how the PostsGroup objects are created from
  *   PostLike objects
  * @param globals
  *   DObj containing the global setting for this site
  */
final class GroupType(style: GroupStyle, globals: DObj):

  /** PostsGroup objects of this style */
  protected val groups = LinkedHashMap[String, PostsGroup]()

  /** Defines how posts add themselves to this group type. Usually it's by a
    * combination of specifying group names in the front matter as a string or
    * list, or by indiciating group names in the filename or inside a class
    * variable.
    *
    * @param post
    *   A post object to be added to the groups of this type
    */
  def addToGroups(post: PostLike): Unit =
    // names of categories this post belongs to
    val catNames = style.getGroupNames(post)
    // for each category, add this post to it and add this category back to the post
    for cat <- catNames do
      groups.get(cat) match
        case Some(t) =>
          t.addPost(post)
        case None =>
          groups(cat) = style.groupConstructor(cat)
          groups(cat).addPost(post)
