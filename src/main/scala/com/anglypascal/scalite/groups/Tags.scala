package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

class TagStyle(ctype: String, configs: Obj) extends GroupStyle:

  def groupConstructor(name: String, globals: DObj): PostsGroup = 
    new PostsGroup(ctype, configs)(name, globals)

  def getGroupNames(post: PostLike): Iterable[String] =
    // check the entry in the front matter
    val unslugged = post.getGroupsList(ctype) match
      case s: Str => s.str.trim.split(",").flatMap(_.trim.split(" ")).toList
      case a: Arr => a.arr.flatMap(s => s.str.trim.split(" ")).toList
      case _      => List()
    // slugify the category names
    unslugged

object TagStyle extends GroupConstructor:
  def apply(ctype: String, configs: Obj): GroupStyle =
    new TagStyle(ctype, configs)

