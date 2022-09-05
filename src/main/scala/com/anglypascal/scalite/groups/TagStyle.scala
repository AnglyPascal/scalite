package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.StringProcessors.*

class TagStyle(groupType: String, configs: MObj, globals: IObj)
    extends PostSuperGroup(groupType, configs, globals):

  /** */
  lazy val locals: IObj =
    val temp = MObj(
      "type" -> groupType,
      "url" -> permalink,
      "outputExt" -> outputExt
    )
    temp update _configs
    IObj(temp)

  def createGroup(name: String): Group[PostLike] =
    new PostGroup(groupType, name)(configs, globals)

  def getGroupNames(post: PostLike): Iterable[String] =
    val unslugged = post.getGroupsList(groupType) match
      case s: DStr => s.str.trim.split(",").flatMap(_.trim.split(" "))
      case a: DArr => a.arr.flatMap(_.getStr).flatMap(_.trim.split(" ")).toArray
      case _       => Array[String]()
    unslugged

object TagStyle extends GroupStyle[PostLike]:

  val styleName = "tag"

  def apply(groupType: String, configs: MObj, globals: IObj) =
    new TagStyle(groupType, configs, globals)
