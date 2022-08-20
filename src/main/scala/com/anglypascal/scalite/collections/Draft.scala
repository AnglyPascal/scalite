package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.data.DObj

class Draft(
    parentDir: String,
    relativePath: String,
    globals: DObj
) extends Post(parentDir, relativePath, globals) // except for the date


object Draft extends ItemConstructor[Draft]:
  def apply(parentDir: String, relativePath: String, globals: DObj): Draft =
    new Draft(parentDir, relativePath, globals)

/** TODO: date will be the motified date collected from the file informations.
  */
object Drafts extends Collection[Draft](Draft):
  val name = "drafts"
