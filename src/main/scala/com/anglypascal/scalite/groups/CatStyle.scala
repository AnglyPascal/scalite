package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.StringProcessors.*

import scala.collection.mutable.LinkedHashMap

class CatStyle(gType: String, configs: MObj, globals: IObj) extends GroupStyle:

  /** */
  def groupConstructor(name: String): PostsGroup =
    new PostsGroup(gType, configs, globals)(name)

  def getGroupNames(post: PostLike): Iterable[String] =
    // process the filepath first
    val arr = post.relativePath.split("/").init.filter(_ != "")
    // check the entry in the front matter
    val unslugged = post.getGroupsList(gType) match
      case s: DStr => arr ++ s.str.trim.split(",").map(_.trim)
      case a: DArr => arr ++ a.arr.flatMap(_.getStr)
      case _       => arr
    // slugify the category names
    unslugged

object CatStyle extends GroupConstructor:

  val styleName = "category"

  def apply(gType: String, configs: MObj, globals: IObj): GroupStyle =
    new CatStyle(gType, configs, globals)
