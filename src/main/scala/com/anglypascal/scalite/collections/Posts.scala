package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.getListOfFiles
import com.anglypascal.scalite.utils.getFileName
import com.anglypascal.scalite.data.Data

/** Companion object that creates the Posts collection. */
object Posts extends Collection[Post]:

  val name = "posts"

  // Default value of sortBy. Updated by the configuration
  sortBy = "date"

  /** TODO: This is suspicious. Will it be able to find all the files in the
    * given directory? files might be in subdirs as well.
    */
  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (getFileName(fn), post)
    items = files.filter(Converters.hasConverter).map(f).toMap
