package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.utils.getListOfFiles
import com.anglypascal.scalite.converters.Converter

import com.rallyhealth.weejson.v1.Obj

class Draft(filename: String, globals: Obj)
    extends Post(filename, globals) // except for the date

/** TODO: Draft posts in _draft folder. These will be rendered in the drafts:
  * true option is set in the global settings. The time variable for these will
  * be the motified date collected from the file informations.
  */
object Draft extends Collection[Post]:

  def things = _drafts
  private var _drafts: Map[String, Post] = _

  val name = "drafts"

  def apply(directory: String, globals: Obj): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (post.title, post)
    _drafts = files.filter(Converter.hasConverter).map(f).toMap

    things

  def render: Unit = ???

