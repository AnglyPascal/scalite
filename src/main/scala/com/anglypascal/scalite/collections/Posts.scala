package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.data.Data

/** Companion object that creates the Posts collection. */
object Posts extends Collection[Post]:

  /** name of posts collection */
  val name = "posts"

  // Default value of sortBy. Updated by the configuration
  sortBy = "date"

  /** Find all the files from the directory and create Post objects */
  def apply(directory: String, globals: DObj) =
    val files = getListOfFilepaths(directory)
    def f(fn: String) =
      val post = new Post(directory, fn, globals)
      post.processGroups()
      (getFileName(fn), post)
    items = files.filter(Converters.hasConverter).map(f).toMap
