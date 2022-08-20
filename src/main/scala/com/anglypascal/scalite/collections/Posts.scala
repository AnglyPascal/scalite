package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.data.Data

/** Companion object that creates the Posts collection. */
object Posts extends Collection[Post](Post):

  /** name of posts collection */
  val name = "posts"

  // Default value of sortBy. Updated by the configuration
  sortBy = "date"

  /** Find all the files from the directory and create Post objects */
