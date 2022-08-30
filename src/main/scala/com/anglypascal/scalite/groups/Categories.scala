package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

class CatStyle(ctype: String, configs: Obj) extends GroupStyle:

  /** */
  def groupConstructor(name: String, globals: DObj): PostsGroup =
    new PostsGroup(ctype, configs)(name, globals)

  def getGroupNames(post: PostLike): Iterable[String] =
    // process the filepath first
    val arr = post.relativePath.split("/").init.filter(_ != "")
    // check the entry in the front matter
    val unslugged = post.getGroupsList(ctype) match
      case s: Str => arr ++ s.str.split(",").map(_.trim) // more options?
      case a: Arr => arr ++ a.arr.map(s => s.str) // error-prone
      case _      => arr
    // slugify the category names
    unslugged

object CatStyle extends GroupConstructor:
  def apply(ctype: String, configs: Obj): GroupStyle =
    new CatStyle(ctype, configs)
