package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.Post
import com.anglypascal.scalite.utils.slugify

import com.rallyhealth.weejson.v1.{Str, Arr, Obj}
import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.utils.DObj
import com.anglypascal.scalite.utils.DStr

object Tag extends Group("tag"):

  /** Tag defines a group of posts. A post can have multiple tags, and each tag
    * can have multiple posts with this tag.
    *
    * An user can add a tag "tag" to a post by adding "tag" in a space separated
    * string or a list under the "tags" entry in the front matter of the post.
    *
    * Note that, to add a tag that has a name with a space, you need to put
    * quotations around that tag name and use a list instead of a space
    * separated string. "tags: this is a tag" will be interpreted as a list that
    * is ["this", "is", "a", "tag"]
    *
    * @constructor
    *   Create a new Tag
    * @param name
    *   name of this tag
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  class Tag(name: String, globals: DObj) extends GroupType(name, globals):
    // define the abstract memebers 
    override val locals: DObj = DObj (
      "title" -> DStr(name),
    )



  /** Map holding all the tags in this website */
  private val tags = LinkedHashMap[String, Tag]()

  /** Add a post to all the tags it has. If the tag doesn't already exsit,
    * create a new instance of Tag to represent it, and add it to tags.
    *
    * @param post
    *   the post to be added in the tags
    * @param globals
    *   a weejson obj containing the global options
    */
  def addToGroups(post: Post, globals: DObj): Unit =
    // names of tags this post has
    val tagNames = getGroupNames(post)
    // for each tag, add this post to it and add this tag back to the post
    for tag <- tagNames do
      tags.get(tag) match
        case Some(t) =>
          t.addPost(post)
          post.addGroup("tag")(t)
        case None =>
          val t = new Tag(tag, globals)
          tags(tag) = t
          post.addGroup("tag")(t)

  /** Process the names of the tags this post belongs to by examining it's tags
    * front matter entry. It also slugifies the category names to make it
    * universal.
    *
    * @param post
    *   a post to be added to the tags
    * @return
    *   an iterator with all the names of the tags
    */
  private def getGroupNames(post: Post): Iterable[String] =
    // check the entry in the front matter
    val unslugged = post.getGroupsList("tags") match
      case s: Str => s.str.split(" ").toList
      case a: Arr => a.arr.flatMap(s => s.str.split(" ")).toList
      case _      => List()
    // slugify the category names
    unslugged.map(slugify(_))

  /** TODO: slugified but then the tag names will be lost :( need to add a way
    * to retrieve properly capitalized tag name. Also need to provide support
    * for case sensitive slugify
    */
