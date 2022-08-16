package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.utils.getListOfFiles


class Draft(filename: String, globals: DObj)
    extends Post(filename, globals) // except for the date

/** TODO: Draft posts in _draft folder. These will be rendered in the drafts:
  * true option is set in the global settings. The time variable for these will
  * be the motified date collected from the file informations.
  */
object Drafts extends Collection[Post]:

  val name = "drafts"

  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (post.title, post)
    items = files.filter(Converters.hasConverter).map(f).toMap
