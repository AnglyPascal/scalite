package com.anglypascal.scalite.readers

import com.anglypascal.scalite.documents.{Post, Layout}
import com.anglypascal.scalite.collections.Tag
import com.anglypascal.scalite.converters.hasConverter
import scala.collection.mutable.LinkedHashMap
import com.rallyhealth.weejson.v1.Obj

/** Reads all the posts in _posts directory */
class PostsReader(directory: String, globals: Obj) extends DirectoryReader[Post](directory):

  /** Just create the posts from the files in the directory, filtering out the
    * files not supported by the converters
    */
  def getObjectMap(layouts: Map[String, Layout]): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = Post(fn, layouts, globals)
      (post.title, post)
    files.filter(hasConverter).map(f).toMap

  /** Add each post to the Tag collections. If the Tag object for the tag name
    * doesn't exist, create it.
    */
  def getObjectMap(
      layouts: Map[String, Layout],
      tags: LinkedHashMap[String, Tag]
  ): Map[String, Post] =
    val posts = getObjectMap(layouts)
    for
      (s, p) <- posts
      t <- p.tagNames
    do
      if tags.contains(t) then 
        tags(t).add(p)
        p.addTag(t, tags(t))
      else
        val tag = new Tag(t)
        tag.add(p)
        p.addTag(t, tag)
        tags += (t -> tag)
    posts

/** Companion object to allow PostsReader(dir, layouts, tags) to return the
  * posts
  */
object PostsReader:
  def apply(
      directory: String,
      layouts: Map[String, Layout],
      tags: LinkedHashMap[String, Tag],
      globals: Obj
  ): Map[String, Post] =
    (new PostsReader(directory, globals)).getObjectMap(layouts, tags)
