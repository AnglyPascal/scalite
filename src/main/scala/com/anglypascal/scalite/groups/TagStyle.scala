package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

class TagStyle(ctype: String, configs: MObj, globals: IObj) extends GroupStyle:

  def groupConstructor(name: String): PostsGroup =
    new PostsGroup(ctype, configs, globals)(name)

  def getGroupNames(post: PostLike): Iterable[String] =
    // check the entry in the front matter
    val unslugged = post.getGroupsList(ctype) match
      case s: DStr => s.str.trim.split(",").flatMap(_.trim.split(" "))
      case a: DArr => a.arr.flatMap(_.getStr).flatMap(_.trim.split(" ")).toArray
      case _       => Array[String]()
    // slugify the category names
    unslugged

object TagStyle extends GroupConstructor:

  val styleName = "tag"

  def apply(gType: String, configs: MObj, globals: IObj): GroupStyle =
    new TagStyle(gType, configs, globals)
