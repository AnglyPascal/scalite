package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

class TagStyle(ctype: String, configs: Obj, globals: DObj) extends GroupStyle:

  def groupConstructor(name: String): PostsGroup =
    new PostsGroup(ctype, configs, globals)(name)

  def getGroupNames(post: PostLike): Iterable[String] =
    // check the entry in the front matter
    val unslugged = post.getGroupsList(ctype) match
      case s: Str => s.str.trim.split(",").flatMap(_.trim.split(" ")).toList
      case a: Arr => a.arr.flatMap(s => s.str.trim.split(" ")).toList
      case _      => List()
    // slugify the category names
    unslugged

object TagStyle extends GroupConstructor:

  val styleName = "tag"

  def apply(gType: String, configs: Obj, globals: DObj): GroupStyle =
    new TagStyle(gType, configs, globals)
