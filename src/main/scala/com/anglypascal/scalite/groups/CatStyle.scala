package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

class CatStyle(groupType: String, configs: MObj, globals: IObj)
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
    val arr = post.relativePath.split("/").init.filter(_ != "")
    val unslugged = post.getGroupsList(groupType) match
      case s: DStr => arr ++ s.str.trim.split(",").map(_.trim)
      case a: DArr => arr ++ a.arr.flatMap(_.getStr)
      case _       => arr
    unslugged

object CatStyle extends GroupStyle[PostLike]:

  val styleName = "category"

  def apply(groupType: String, configs: MObj, globals: IObj) =
    new CatStyle(groupType, configs, globals)
