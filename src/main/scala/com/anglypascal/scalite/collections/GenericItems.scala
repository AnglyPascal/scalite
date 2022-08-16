package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.{DObj, DStr}
import com.anglypascal.scalite.utils.getListOfFiles

/** Defines the collection of generic item */
class GenericCollection(val name: String) extends Collection[Item]:

  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val item = new GenericItem(fn, globals)
      (item.title, item)
    items = files.map(f).toMap
