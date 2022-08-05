package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.Post
import com.anglypascal.scalite.utils.slugify

import com.rallyhealth.weejson.v1.{Str, Arr, Obj}
import scala.collection.mutable.LinkedHashMap

/** Categories can be part of the post url, and also posts nested in a subfolder
  * structure will automatically have the folder names as categories
  */
class Category(name: String, globals: Obj)
    extends PostsBag("category", name, globals)

/** Companion object of category 
 */
object Category extends BagHandler[Category]:

  val bags = LinkedHashMap[String, Category]()

  def addToBags(post: Post, globals: Obj): Unit =
    val catNames = getNames(post)

    for cat <- catNames do
      bags.get(cat) match
        case Some(t) =>
          t.addItem(post)
          post.addBag("category")(t)
        case None =>
          val t = new Category(cat, globals)
          bags(cat) = t
          post.addBag("category")(t)

  def getNames(post: Post): Iterable[String] =
    val arr = post.filename.split("/").tail.init
    post.getBagsList("category") match
      case s: Str => arr ++ s.str.split(", ").map(slugify(_)) // more options?
      case a: Arr => arr ++ a.arr.map(s => slugify(s.str)) // error-prone
      case _      => arr

  /** TODO: slugify the category names to uniformify them */
