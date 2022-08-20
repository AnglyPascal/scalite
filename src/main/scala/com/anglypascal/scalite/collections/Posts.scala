package com.anglypascal.scalite.collections

/** Companion object that creates the Posts collection. */
object Posts extends Collection[Post](Post):

  /** name of posts collection */
  val name = "posts"

  // Default value of sortBy. Updated by the configuration
  sortBy = "date"
