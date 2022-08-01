package com.anglypascal.scalite.readers

import com.anglypascal.scalite.{Post, Layout}

class PostReader(directory: String) extends DirectoryReader[Post](directory):
  /** */
  def getObjectMap(layouts: Map[String, Layout]): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn)
      (post.title, post)

    val map = files.map(f).toMap
    for (s, l) <- map do l.set_parent(layouts)
    map

object PostReader:
  def apply(directory: String, layouts: Map[String, Layout]) =
    (new PostReader(directory)).getObjectMap(layouts)
