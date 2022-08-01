package com.anglypascal.scalite.collections

import com.anglypascal.scalite.{Globals, Collection, Page, Post, Layout}
import scala.collection.mutable.Set
import com.rallyhealth.weejson.v1.{Obj, Arr}

class Tag(val name: String) extends Collection[Post] with Page:

  val parent_name = 
    if Globals.site.obj.contains("tag_layout") then 
      Globals.site("tag_layout").str
    else "tag"

  val things = Set[Post]()

  private def postToItem(post: Post): Obj = 
    Obj("post_title" -> post.title)

  def render(context: Obj, partials: Map[String, Layout]): String = 
    context("items") = Arr(things.toList.map(postToItem))
    parent match
      case l: Layout =>
        l.render(context, partials)
      case null => "" // TODO throw an error, saying layout not found


