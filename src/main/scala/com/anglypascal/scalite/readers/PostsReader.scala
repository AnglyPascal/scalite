package com.anglypascal.scalite.readers

import com.anglypascal.scalite.{Post, Layout}
import com.anglypascal.scalite.collections.Tag

class PostReader(directory: String) extends DirectoryReader[Post](directory):
  /** */
  def getObjectMap(layouts: Map[String, Layout]): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn)
      (post.title, post)

    val map = files.map(f).toMap
    for (s, p) <- map do p.set_parent(layouts)
    map

  def getObjectMap(
      layouts: Map[String, Layout],
      tags: Map[String, Tag]
  ): Map[String, Post] =
    val posts = getObjectMap(layouts)
    for
      (s, p) <- posts
      t <- p.obj("tags").arr
      if tags.contains(t.str)
    do tags(t.str).add(p)
    posts

object PostReader:
  def apply(
      directory: String,
      layouts: Map[String, Layout],
      tags: Map[String, Tag]
  ): Map[String, Post] =
    (new PostReader(directory)).getObjectMap(layouts, tags)
