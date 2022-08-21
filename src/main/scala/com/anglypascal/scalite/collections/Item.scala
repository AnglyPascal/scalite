package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Reader

/** Abstract class for a generic Item that simply has a source file to read from
  * and some internal variables defined in locals to process the contents of
  * that file with method render.
  *
  * @constructor
  *   creates a new Item from the given path to the source file
  *
  * @param parentDir
  *   path to the directory of the Collection this Item is in
  * @param relativePath
  *   path to the source file of this Item relative to the parentDir
  * @param globals
  *   global variables
  * @param colName
  *   name of the collection this item is part of
  */
abstract class Item(
    val parentDir: String,
    val relativePath: String,
    private val globals: DObj,
    private val collection: DObj
) extends Reader(parentDir + relativePath):

  /** local variales for this item */
  def locals: DObj

  /** Process the contents of this item */
  protected lazy val render: String

  lazy val visible: Boolean

trait ItemConstructor[A <: Item]:
  def apply(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): A
