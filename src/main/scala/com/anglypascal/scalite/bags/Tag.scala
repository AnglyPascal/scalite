package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.Post

import com.rallyhealth.weejson.v1.{Str, Arr, Obj}
import scala.collection.mutable.LinkedHashMap

/** Tag are less "powerful" than categories, and can't be part of the post url
  */
class Tag(name: String, globals: Obj)
    extends PostsBag("tag", name, globals)

/** Companion object
  */
object Tag extends BagHandler[Tag]:

  val bags = LinkedHashMap[String, Tag]()

  def addToBags(post: Post, globals: Obj): Unit =
    val tagNames = getNames(post)
    for tag <- tagNames do
      bags.get(tag) match
        case Some(t) =>
          t.addItem(post)
          post.addBag("tag")(t)
        case None =>
          val t = new Tag(tag, globals)
          bags(tag) = t
          post.addBag("tag")(t)

  def getNames(post: Post): Iterable[String] =
    post.getBagsList("tag") match
      case s: Str => s.str.split(" ")
      case a: Arr => a.arr.flatMap(s => s.str.split(" "))
      case _      => List()

  /** TODO: lowercase the tag names to uniformify them */
