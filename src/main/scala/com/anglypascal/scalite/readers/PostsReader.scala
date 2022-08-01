package com.anglypascal.scalite.readers

import com.anglypascal.scalite.{Post, Layout}
import com.anglypascal.scalite.collections.Tag
import com.anglypascal.scalite.converters.hasConverter
import scala.collection.mutable.LinkedHashMap

class PostsReader(directory: String) extends DirectoryReader[Post](directory):
  /** */
  def getObjectMap(layouts: Map[String, Layout]): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn)
      (post.title, post)

    val map = files.filter(hasConverter).map(f).toMap
    for (s, p) <- map do p.set_parent(layouts)
    map

  def getObjectMap(
      layouts: Map[String, Layout],
      tags: LinkedHashMap[String, Tag]
  ): Map[String, Post] =
    val posts = getObjectMap(layouts)
    for
      (s, p) <- posts
      t <- p.obj("tags").arr
    do
      if tags.contains(t.str) then tags(t.str).add(p)
      else
        tags += (t.str -> new Tag(t.str))
        tags(t.str).add(p)
    posts

object PostsReader:
  def apply(
      directory: String,
      layouts: Map[String, Layout],
      tags: LinkedHashMap[String, Tag]
  ): Map[String, Post] =
    (new PostsReader(directory)).getObjectMap(layouts, tags)
