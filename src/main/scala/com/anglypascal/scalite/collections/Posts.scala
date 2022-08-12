package com.anglypascal.scalite.collections

import com.anglypascal.scalite.utils.{DObj, DStr, getListOfFiles}
import com.anglypascal.scalite.converters.Converter

/** Companion object that creates the Posts collection.
  */
object Posts extends Collection[Post]:

  def things = _posts
  private var _posts: Map[String, Post] = _

  val name = "posts"

  def apply(directory: String, globals: DObj): Map[String, Post] =
    val files = getListOfFiles(directory)
    def f(fn: String) =
      val post = new Post(fn, globals)
      post.processGroups()
      (post.title, post)
    _posts = files.filter(Converter.hasConverter).map(f).toMap
    things

  /** By default posts are sorted by date. But this can be changed by updating
    * the sortBy key in the collections configuration.
    */
  private var _sortBy = "date"
  override def sortBy: String = _sortBy
  override def sortBy_=(key: String): Unit = _sortBy = key

  private def compareBy(fst: Post, snd: Post, key: String): Int =
    val g1 = fst.locals.get(key)
    val g2 = snd.locals.get(key)
    g1 match
      case None =>
        g2 match
          case None    => 0
          case Some(_) => -1
      case Some(s1): Some[DStr] =>
        g2 match
          case None                 => 1
          case Some(s2): Some[DStr] => s1.str compare s2.str
          case _                    => 0
      case _ => 0

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  def compare(fst: Post, snd: Post): Int =
    val c = compareBy(fst, snd, sortBy)
    if c != 0 then return c
    compareBy(fst, snd, "title")

  /** sorts out the posts, renders them with the globals, and writes them to the
    * disk
    */
  def render: Unit = ???
