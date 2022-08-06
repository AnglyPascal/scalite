package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.Post
import com.anglypascal.scalite.utils.slugify

import com.rallyhealth.weejson.v1.{Str, Arr, Obj}
import scala.collection.mutable.LinkedHashMap

object Category extends Bag("category"):

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
  type Category = BagType

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
  def addToBags(post: Post, globals: Obj): Unit =
    // names of categories this post belongs to
    val catNames = getBagNames(post)
    // for each category, add this post to it and add this category back to the post
    for cat <- catNames do
      categories.get(cat) match
        case Some(t) =>
          t.addPost(post)
          post.addBag("category")(t)
        case None =>
          val t = new Category(cat, globals)
          categories(cat) = t
          post.addBag("category")(t)

  /** Process the names of the categories this post belongs to by examining it's
    * categories front matter entry and it's filepath. It also slugifies the
    * category names to make it universal.
    *
    * @param post
    *   a post to be added to the categories
    * @return
    *   an iterator with all the names of the categories
    */
  private def getBagNames(post: Post): Iterable[String] =
    // process the filepath first
    val arr = post.filepath.split("/").tail.init
    // check the entry in the front matter
    val unslugged = post.getBagsList("categories") match
      case s: Str => arr ++ s.str.split(", ") // more options?
      case a: Arr => arr ++ a.arr.map(s => s.str) // error-prone
      case _      => arr
    // slugify the category names
    unslugged.map(slugify(_))

  /** TODO: give more options in slugify? */
