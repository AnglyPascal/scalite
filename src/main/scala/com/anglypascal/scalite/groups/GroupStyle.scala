package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.plugins.Plugin

/** Defines how PostsGroup objects are created from PostLike objects */
trait GroupStyle:

  /** Given the name, create a new PostsGroup */
  def groupConstructor(name: String): PostsGroup

  /** Process the names of the PostsGroups this PostLike belongs to by examining
    * it's frontMatter entry
    *
    * @param post
    *   a PostLike to be added to the this PostGroup style
    * @return
    *   an iterator with all the names of the PostGroups of this style
    */
  def getGroupNames(post: PostLike): Iterable[String]

/** Constructs a new GroupStyle. To add custom GroupStyle, one needs to provide
  * an implementaiton of this constructor
  */
trait GroupConstructor extends Plugin:

  /** Name of this GroupStyle */
  val styleName: String

  /** Given group type, group configs and global configs, create a new
    * GroupStyle
    *
    * @param gType
    *   String name of the type of the group, like "tags"
    * @param configs
    *   Mutable DObj containing customization set in groups/gType section
    * @param globals
    *   Immutable DObj containing global configuration
    * @returns
    *   New GroupStyle conforming to these configurations
    */
  def apply(gType: String, configs: MObj, globals: IObj): GroupStyle
