package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.plugins.Plugin
import com.rallyhealth.weejson.v1.Obj

/** Defines how PostsGroup objects are created from PostLike objects */
trait GroupStyle:

  /** Given the name, create a new PostsGroup */
  def groupConstructor(name: String): PostsGroup

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

/** Constructs a new GroupStyle. To add custom GroupStyle, one needs to provide an
 *  implementaiton of this constructor */
trait GroupConstructor extends Plugin:
  
  val styleName: String
  
  /** Given group type, group configs and global configs, create a new
    * GroupStyle
    *
    * @param gType
    *   String name of the type of the group, like "tags"
    * @param configs
    *   Customization set in the groups.gType section in \_configs.yml
    * @param globals
    *   DObj containing global configuration
    * @returns
    *   New GroupStyle conforming to these configurations
    */
  def apply(gType: String, configs: Obj, globals: DObj): GroupStyle
