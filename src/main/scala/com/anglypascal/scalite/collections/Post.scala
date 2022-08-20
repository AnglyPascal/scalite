package com.anglypascal.scalite.collections

import com.anglypascal.scalite.NoLayoutException
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.*
import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.groups.Group
import com.anglypascal.scalite.groups.PostsGroup
import com.anglypascal.scalite.utils.StringProcessors.*
import com.anglypascal.scalite.utils.DateParser.*
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.LinkedHashMap
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.Defaults

/** Reads the content of a post file and prepares a Post object.
  *
  * @param parentDir
  *   absolute path to the root folder containing posts
  * @param relativePath
  *   path to the post file relative to the parentDir
  * @param globals
  *   a weejson object passed through the "_config.yml" file
  */
class Post(
    parentDir: String,
    relativePath: String,
    globals: DObj
) extends Item(parentDir, relativePath, globals)
    with ReaderOps
    with Page:

  private val logger = Logger("Post")

  /** Get the parent layout name, if it exists. Layouts might not have a parent
    * layout, but each post needs to have one.
    */
  protected val parentName =
    front_matter.obj.remove("layout") match
      case Some(s) =>
        s match
          case s: Str => s.str
          case _ =>
            logger.error(
              s"Please specify a valid layout for post $filepath" +
                "falling back to default layout: post"
            )
            "post"
      case None => "post"

  /** Search for the parent layout in Layout.layouts */
  _parent = Layouts.layouts.get(parentName)

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filepath. If the filepath has no title given, simply
    * name this post "untitled"
    */
  val title: String =
    front_matter.extractOrElse("title")(
      titleParser(filepath)
        .map(titlify(_))
        .getOrElse("Untitled" + this.toString)
    ) // so that titles are always different for different posts

  /** The date in front_matter may have extra information like time and
    * time-zone. Nothing is necessary, but if date is being given, it has to be
    * given in full, if time is given, it has to be given in full.
    */
  lazy val date = urlObj.getOrElse("dateString")("undated")

  /** TODO: Will later add support for getting the modified time values in the
    * case when the date/time is not specified in either the title name or the
    * front_matter
    *
    * Maybe DraftPost will extend Post overriding this time handling thing
    */
  private lazy val urlObj: DObj =
    val dateString = front_matter.extractOrElse("date")(filepath)
    val dateFormat =
      front_matter.extractOrElse("dateFormat")(
        globals.getOrElse("dateFormat")("yyyy-MM-dd")
      )
    val obj = dateParseObj(dateString, dateFormat)
    obj("title") = title
    obj("modified_time") = lastModifiedTime(dateFormat)
    obj("outputExt") = outputExt
    obj("collection") =
      globals.getOrElse("collection")(DObj()).getOrElse("name")("posts")
    // TODO slugs
    DObj(obj)

  /** Template for the permalink of the post */
  private lazy val permalinkTemplate =
    front_matter.extractOrElse("permalinkTemplate")(
      globals
        .getOrElse("collection")(DObj())
        .getOrElse("permalinkTemplate")(
          globals.getOrElse("permalinkTemplate")(
            Defaults.permalinkTemplate
          )
        )
    )

  lazy val _permalink = URL(permalinkTemplate)(urlObj)
  def permalink = _permalink

  /** Returns whether to render this post or not. Default is true. Putting
    * output: false inside collection.post complete turns off rendering of
    * posts.
    */
  val visible: Boolean = front_matter.extractOrElse("visible")(true)

  private lazy val _outputExt: String =
    front_matter.extractOrElse("outputExt")(
      Converters
        .findByExt(filepath)
        .map(_.outputExt)
        .getOrElse(".html")
    )
  def outputExt = _outputExt

  lazy val locals =
    front_matter.obj ++= List(
      "title" -> title,
      "date" -> date,
      "url" -> permalink
    )
    front_matter.obj.get("showExcerpt") match
      case Some(some) =>
        some match
          case some: Bool =>
            if some.bool then front_matter("excerpt") = excerpt
          case _ => ()
      case _ => ()

    DObj(front_matter)

  /** Get the posts from the front\_matter and get their permalinks
    * @example
    *   {{{
    * postUrls:
    *   post1: 2022-04-01-post-name
    *   post2: 2013-02-23-another-post-name
    *   }}}
    *   These links then can be used as mustache or other tags like {{post1}}
    */
  def postUrls: Map[String, String] =
    def f(p: (String, Value)): List[(String, String)] =
      p._2 match
        case str: Str =>
          Posts.items.get(str.str) match
            case Some(post) =>
              List(p._1 -> post.permalink)
            case None => List()
        case _ => List()
    front_matter.obj.remove("postUrls") match
      case None => Map()
      case Some(v) =>
        v match
          case v: Obj => v.obj.flatMap(f).toMap
          case _      => Map()

  /** Convert the contents of the post to HTML, throwing an exception on failure
    */
  protected def render: String =
    /** call to postUrls */
    val str = Converters.convert(main_matter, filepath)
    val context = DObj(
      postUrls.map(p => (p._1, DStr(p._2))) ++
        Map("site" -> globals, "post" -> locals)
    )
    parent match
      case Some(l) => l.render(context, str)
      case None    => str

  /** TODO: if showExcerpt is true, then create an excerpt object here? And add
    * the excerpt to the obj.
    *
    * For now, leave it simple like this
    */
  def excerpt: String =
    val head = getExcerpt(main_matter, "separateor")
    Converters.convert(head, filepath)

  /** Return the global settings for the collection-type grpType */
  def getGroupsList(grpType: String): Value =
    front_matter.obj.remove(grpType) match
      case Some(v) => v
      case None    => null

  /** Adds the collection in the set of this collection-type */
  def addGroup[A <: PostsGroup](grpType: String)(a: A): Unit =
    if groups.contains(grpType) then groups(grpType) += a
    else groups += grpType -> Set(a)

  /** The map holding sets of collection-types */
  private val groups = LinkedHashMap[String, Set[PostsGroup]]()

  /** Processes the collections this post belongs to, for the collections
    * specified in the list in CollectionsHandler companion object
    */
  for groupObj <- Group.availableGroups do groupObj.addToGroups(this, globals)

object Post extends ItemConstructor[Post]:
  def apply(parentDir: String, relativePath: String, globals: DObj): Post =
    new Post(parentDir, relativePath, globals)
