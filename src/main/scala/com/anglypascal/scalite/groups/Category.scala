package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.Post
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.LinkedHashMap

object Category extends Group("categories"):

  /** Category defines a group of posts. A post can belong to multiple
    * categories, and each Category can have multiple posts belong to it.
    *
    * An user can add a post to a category "cat" in two ways:
    *   - By adding "cat" in a comma separated string or a list under the
    *     "categories" entry in the front matter of the post
    *   - By putting the post under the "_posts/cat/..." directory
    *
    * If there are multiple categories listed in the "categories" tag in the
    * front matter, like ["cat1", "cat2", "cat3"] or "cat1, cat2, cat3", then
    * the relative url of the post will be "/cat1/cat2/cat3/...". That is, the
    * categories will form a hierarchy for that post.
    *
    * @constructor
    *   Create a new Category
    * @param name
    *   name of this category
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  class Category(name: String, globals: DObj) extends GroupType(name, globals):
    /** */
    lazy val permalink: String = ???

    protected lazy val outputExt = ".html"

  /** Map holding all the categories in this website */
  private val categories = LinkedHashMap[String, Category]()

  /** Add a post to all the categories it belongs to. If the category doesn't
    * already exsit, create a new instance of Category to represent it, and add
    * it to categories
    *
    * @param post
    *   the post to be added in the categories
    * @param globals
    *   a weejson obj containing the global options
    */
  def addToGroups(post: Post, globals: DObj): Unit =
    // names of categories this post belongs to
    val catNames = getGroupNames(post)
    // for each category, add this post to it and add this category back to the post
    for cat <- catNames do
      categories.get(cat) match
        case Some(t) =>
          t.addPost(post)
          post.addGroup(ctype)(t)
        case None =>
          val t = new Category(cat, globals)
          categories(cat) = t
          post.addGroup(ctype)(t)

  /** Process the names of the categories this post belongs to by examining it's
    * categories front matter entry and it's filepath. It also slugifies the
    * category names to make it universal.
    *
    * @param post
    *   a post to be added to the categories
    * @return
    *   an iterator with all the names of the categories
    */
  private def getGroupNames(post: Post): Iterable[String] =
    // process the filepath first
    val arr = post.relativePath.split("/").init.filter(_ != "")
    // check the entry in the front matter
    val unslugged = post.getGroupsList(ctype) match
      case s: Str => arr ++ s.str.split(",").map(_.trim) // more options?
      case a: Arr => arr ++ a.arr.map(s => s.str) // error-prone
      case _      => arr
    // slugify the category names
    unslugged.map(slugify(_, "pretty", true))

  /** TODO: give more options in slugify? */
