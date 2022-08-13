package com.anglypascal.scalite.collections

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.utils.getListOfFiles
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.converters.Converters


class Draft(filename: String, globals: DObj)
    extends Post(filename, globals) // except for the date

/** TODO: Draft posts in _draft folder. These will be rendered in the drafts:
  * true option is set in the global settings. The time variable for these will
  * be the motified date collected from the file informations.
  */
object Drafts extends Collection[Post]:

  def things = _drafts
  private var _drafts: Map[String, Post] = _

  val name = "drafts"

  def apply(directory: String, globals: DObj): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (post.title, post)
    _drafts = files.filter(Converters.hasConverter).map(f).toMap

    things

  def compare(fst: Post, snd: Post): Int = Posts.compare(fst, snd)

  def process: Unit = ???

  def render: String = ???

