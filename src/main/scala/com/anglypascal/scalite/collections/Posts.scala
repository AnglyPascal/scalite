package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.getListOfFiles
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.plugins.Plugin

/** Companion object that creates the Posts collection.
  */
object Posts extends Collection[Post] with Plugin:

  val name = "posts"

  /** By default posts are sorted by date. But this can be changed by updating
    * the sortBy key in the collections configuration.
    */
  sortBy = "date"

  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (post.title, post)
    items = files.filter(Converters.hasConverter).map(f).toMap
