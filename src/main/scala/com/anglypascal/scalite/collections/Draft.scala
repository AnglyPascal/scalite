package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths

class Draft(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    colName: String
) extends Post(parentDir, relativePath, globals, colName) // except for the date

/** TODO: date will be the motified date collected from the file informations.
  */
object Drafts extends Collection[Post]:

  val name = "drafts"

  def apply(directory: String, globals: DObj) =
    val files = getListOfFilepaths(directory)
    def f(fn: String) =
      val draft = new Draft(directory, fn, globals, name)
      draft.processGroups()
      (draft.title, draft)
    items = files.filter(Converters.hasConverter).map(f).toMap
