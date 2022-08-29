package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.data.DObj

class Draft(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    collection: DObj,
    rType: String
) extends Post(
      parentDir,
      relativePath,
      globals,
      collection,
      rType
    )

object Draft extends ItemConstructor[Draft]:
  def apply(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj,
      rType: String
  ): Draft =
    new Draft(parentDir, relativePath, globals, collection, rType)

/** TODO: date will be the motified date */
object Drafts extends Collection[Draft](Draft)("drafts")
