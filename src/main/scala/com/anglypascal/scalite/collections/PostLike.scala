package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.documents.Pages
import com.anglypascal.scalite.groups.Groups
import com.anglypascal.scalite.groups.PostsGroup
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DateParser.dateParseObj
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.*
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer

/** Reads the content of a post file and prepares a Post object.
  *
  * @param rType
  *   type of this post, used in ScopedDefaults
  * @param parentDir
  *   absolute path to the root folder containing posts
  * @param relativePath
  *   path to the post file relative to the parentDir
  * @param globals
  *   DObj containing the global setting for this site
  * @param collection
  *   the configurations passed to the whole collection
  */
class PostLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    globals: DObj,
    collection: DObj
) extends Element
    with Page:

  private val logger = Logger(s"PostLike $rType")
  logger.debug("creating from " + GREEN(filepath))

  /** Get the parent layout name, if it exists. Layouts might not have a parent
    * layout, but each post needs to have one.
    */
  protected val layoutName =
    extractChain(frontMatter, collection)("layout")(rType)

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filepath. If the filepath has no title given, simply
    * name this post "untitled"
    */
  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        titleParser(filename).map(titlify(_)).getOrElse("Untitled")
      )
    )

  /** The date in frontMatter may have extra information like time and
    * time-zone. Nothing is necessary, but if date is being given, it has to be
    * given in full, if time is given, it has to be given in full.
    */
  lazy val date = urlObj.getOrElse("dateString")("undated")

  /** TODO: Will later add support for getting the modified time values in the
    * case when the date/time is not specified in either the title name or the
    * frontMatter
    *
    * Maybe DraftPost will extend Post overriding this time handling thing
    */
  private lazy val urlObj: DObj =
    val dateString = frontMatter.extractOrElse("date")(filename)
    val dateFormat =
      extractChain(frontMatter, collection, globals)(
        "dateFormat"
      )(Defaults.dateFormat)
    val obj = dateParseObj(dateString, dateFormat)

    obj("title") = title
    obj("lastModifiedTime") = lastModifiedTime(filepath, dateFormat)
    obj("outputExt") = outputExt
    obj("filename") = filename
    obj("collection") = collection.getOrElse("name")("posts")
    for (k, s) <- groups do obj(k) = s.map(_.name).mkString("/")

    obj("slugTitle") = slugify(title)
    obj("slugTitlePretty") = slugify(title, "pretty")
    obj("slugTitleCased") = slugify(title, "default", true)

    DObj(obj)

  /** Template for the permalink of the post */
  lazy val permalink =
    val permalinkTemplate =
      frontMatter.extractOrElse("permalink")(
        globals
          .getOrElse("collection")(DObj())
          .getOrElse("permalink")(
            globals.getOrElse("permalink")(
              Defaults.Posts.permalink
              // TODO the scope got a bit messed up here
            )
          )
      )
    purifyUrl(URL(permalinkTemplate)(urlObj))

  /** Returns whether to render this post or not. Default is true. Putting
    * output: false inside collection.post complete turns off rendering of
    * posts.
    */
  val visible = extractChain(frontMatter, collection)("visible")(true)

  protected lazy val outputExt =
    extractChain(frontMatter, collection)(
      "outputExt"
    )(Converters.findExt(filepath))

  lazy val locals =
    frontMatter.obj ++= List(
      "title" -> title,
      "date" -> date,
      "url" -> permalink,
      "filename" -> filename
    )
    if frontMatter.extractOrElse("showExcerpt")(false) then
      frontMatter("excerpt") = excerpt

    DObj(frontMatter).add("collection" -> collection)

  /** Get the posts from the front\_matter and get their permalinks
    *
    * @example
    *   {{{
    * postUrls:
    *   post1: /_posts/2022-04-01-post-name.md
    *   post2: /_posts/cat1/2022-04-01-post-name-2.md
    *   }}}
    *   These links then can be used as mustache or other tags like {{post1}}
    */
  lazy val postUrls: Map[String, String] =
    def f(p: (String, Value)): Option[(String, String)] =
      p._2 match
        case str: Str => Pages.findPage(str.str).map(p._1 -> _.permalink)
        case _        => None
    frontMatter.extractOrElse("postUrls")(Obj()).obj.flatMap(f).toMap

  /** Convert the contents of the post to HTML */
  protected lazy val render: String =
    val str = Converters.convert(mainMatter, filepath)
    val context = DObj(
      postUrls.map(p => (p._1, DStr(p._2))) ++
        Map(
          "site" -> globals,
          "page" -> locals
        )
    )
    layout match
      case Some(l) =>
        logger.debug(s"$this has parent layout ${l.name}")
        l.render(context, str)
      case None =>
        logger.debug(s"$this has no parent layout")
        str

  /** For now, just gets the first part of the main matter, separated by the
    * separator.
    *
    * TODO: if no separator is found, get the first paragraph. Also look into
    * the linking issue discussed in jekyll
    */
  def excerpt: String =
    val separator =
      extractChain(frontMatter, globals)("separator")(Defaults.separator)
    val head = getExcerpt(mainMatter, separator)
    Converters.convert(head, filepath)

  /** The map holding sets of collection-types */
  private val groups = LinkedHashMap[String, ListBuffer[PostsGroup]]()

  /** Return the global settings for the collection-type grpType */
  def getGroupsList(grpType: String): Value =
    frontMatter.obj.remove(grpType).getOrElse(null)

  /** Adds the collection in the set of this collection-type */
  def addGroup[A <: PostsGroup](grpType: String)(a: A): Unit =
    if groups.contains(grpType) then groups(grpType) += a
    else groups += grpType -> ListBuffer(a)

  /** Processes the collections this post belongs to, for the collections
    * specified in the list in CollectionsHandler companion object
    */
  Groups.addToGroups(this)

  override def toString(): String =
    CYAN(title) + "(" + GREEN(date) + ")" + "[" + BLUE(permalink) + "]"

/** Constructor for PostLike objects */
object PostConstructor extends ElemConstructor:

  val styleName = "post"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): Element =
    new PostLike(rType)(parentDir, relativePath, globals, collection)
