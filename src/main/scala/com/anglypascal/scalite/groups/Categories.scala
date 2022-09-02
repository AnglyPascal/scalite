package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

class CatStyle(gType: String, configs: Obj, globals: DObj) extends GroupStyle:

  /** */
  def groupConstructor(name: String): PostsGroup =
    new PostsGroup(gType, configs, globals)(name)

  def getGroupNames(post: PostLike): Iterable[String] =
    // process the filepath first
    val arr = post.relativePath.split("/").init.filter(_ != "")
    // check the entry in the front matter
    val unslugged = post.getGroupsList(gType) match
      case s: Str => arr ++ s.str.split(",").map(_.trim) // more options?
      case a: Arr => arr ++ a.arr.map(s => s.str) // error-prone
      case _      => arr
    // slugify the category names
    unslugged

object CatStyle extends GroupConstructor:

  val styleName = "category"

  def apply(gType: String, configs: Obj, globals: DObj): GroupStyle =
    new CatStyle(gType, configs, globals)
