package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.{DObj, DStr, getListOfFiles}

/** Defines the collection of generic item */
class GenericItems(val name: String) extends Collection[Item]:

  def things = _items
  private var _items: Map[String, Item] = Map()

  def apply(directory: String, globals: DObj) =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val item = new GenericItem(fn, globals)
      (item.title, item)
    _items = files.map(f).toMap
    things

  def compare(fst: Item, snd: Item): Int = ???

  def render: Unit = ???
