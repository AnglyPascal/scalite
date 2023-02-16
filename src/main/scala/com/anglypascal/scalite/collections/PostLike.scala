package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DNull
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.documents.Pages
import com.anglypascal.scalite.hooks.BeforeLocals
import com.anglypascal.scalite.hooks.Hooks
import com.anglypascal.scalite.hooks.PageHooks
import com.anglypascal.scalite.hooks.PostHooks
import com.anglypascal.scalite.trees.PostForests
import com.anglypascal.scalite.trees.AnyTree
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DateParser.dateParseObj
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.*
import com.typesafe.scalalogging.Logger

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import com.anglypascal.scalite.trees.WithTree
import com.anglypascal.scalite.trees.Tree
import com.anglypascal.scalite.documents.Convertible

/** Reads the content of a post file and prepares a Post object.
  *
  * @param rType
  *   type of this post, used in ScopedDefaults
  * @param parentDir
  *   absolute path to the root folder containing posts
  * @param relativePath
  *   path to the post file relative to the parentDir
  * @param globals
  *   IObj containing the global setting for this site
  * @param collection
  *   the configurations passed to the whole collection
  *
  * FIXME: showExcerpt should be defaulted by the collection, and
  * elements should have the ability to turn it off
  */
class PostLike(rType: String)(
    val parentDir: String,
    val relativePath: String,
    protected val globals: IObj,
    private val collection: IObj
) extends Element(rType)
    with Convertible(parentDir + relativePath)
    with Page
    with WithTree[PostLike]:

  private val logger = Logger(s"PostLike \"${CYAN(rType)}\"")
  logger.debug("source: " + GREEN(filepath))

  protected val configs: MObj =
    val c =
      MObj() update collection update frontMatter update MObj(
        "rType" -> rType,
        "parentDir" -> parentDir,
        "relativePath" -> relativePath
      )
    c update PostHooks.beforeInits(globals)(IObj(c))

  /** Get the parent layout name, if it exists. Layouts might not
    * have a parent layout, but each post needs to have one.
    */
  protected lazy val layoutName =
    extractChain(configs, collection)("layout")(rType)

  /** Get the title of the post from the front matter, defaulting
    * back to the title parsed from the filepath. If the filepath
    * has no title given, simply name this post "untitled"
    */
  lazy val title: String =
    configs.extractOrElse("title")(
      configs.extractOrElse("name")(
        titleParser(filename)
          .map(titlify(_))
          .getOrElse("Untitled")
      )
    )

  /** The date in configs may have extra information like time and
    * time-zone. Nothing is necessary, but if date is being given,
    * it has to be given in full, if time is given, it has to be
    * given in full.
    */
  lazy val date = urlObj.getOrElse("dateString")("undated")

  /** TODO: Will later add support for getting the modified time
    * values in the case when the date/time is not specified in
    * either the title name or the configs
    *
    * Maybe DraftPost will extend Post overriding this time
    * handling thing
    */
  private lazy val urlObj: IObj =
    val dateString = configs.extractOrElse("date")(filename)
    val dateFormat = extractChain(configs, collection, globals)(
      "dateFormat"
    )(Defaults.dateFormat)
    val obj = dateParseObj(dateString, dateFormat) update MObj(
      "title" -> title,
      "lastModifiedTime" -> lastModifiedTime(
        filepath,
        dateFormat
      ),
      "outputExt" -> outputExt,
      "filename" -> filename,
      "collection" -> collection.getOrElse("name")("posts"),
      "slugTitle" -> slugify(title),
      "slugTitlePretty" -> slugify(title, "pretty"),
      "slugTitleCased" -> slugify(title, "default", true)
    ) update treeObj

    IObj(obj)

  /** Template for the permalink of the post */
  lazy val permalink =
    val permalinkTemplate =
      configs.extractOrElse("permalink")(
        globals
          .getOrElse("collection")(IObj())
          .getOrElse("permalink")(
            globals.getOrElse("permalink")(
              Defaults.Posts.permalink
              // TODO the scope got a bit messed up here
            )
          )
      )
    purifyUrl(URL(permalinkTemplate)(urlObj))

  /** Returns whether to render this post or not. Can be set in
    * the the front matter of the post, in the defaults of its
    * scope, or in the collections as a global value. Default is
    * true.
    */
  val visible = extractChain(configs, collection)("visible")(true)

  protected lazy val outputExt =
    extractChain(configs, collection)(
      "outputExt"
    )(Converters.findOutputExt(filepath))

  lazy val locals =
    val l = _locals
    if configs.getOrElse("showExcerpt")(false) then
      l += "excerpt" -> excerpt
    IObj(l)

  private def _locals =
    configs update MObj(
      "title" -> title,
      "date" -> date, // TODO Add more time stuff?
      "url" -> permalink,
      "filename" -> filename,
      "collection" -> collection
    )
    // TODO: add time filters, for that, add the UTC timestamp, and have the filters
    // read time from that
    configs update PostHooks.beforeLocals(globals)(IObj(configs))

  /** Extract excerpt from the mainMatter */
  private def excerpt: String =
    val separator =
      extractChain(configs, globals)("separator")(
        Defaults.separator
      )
    Excerpt(
      mainMatter,
      filepath,
      shouldConvert,
      separator
    )(IObj(_locals), globals).content

  /** Prepares hyperlinks to local pages for use in the templates.
    *
    * Picks up all maps "link_name" -> "relative or absolute path
    * to the file" under the hyperlinks section of configs. This
    * link will be available to the templates in the root context
    * as "link name", and can be used as {{link_name}} in
    * mustache.
    *
    * @example
    *   {{{
    * hyperlinks:
    *   post1: /_posts/2022-04-01-post-name.md
    *   post2: /_posts/cat1/2022-04-01-post-name-2.md
    *   }}}
    *   These links then can be used as mustache or other tags
    *   like {{post1}}
    */
  private def postUrls: Map[String, String] =
    def f(p: (String, Data)): Option[(String, String)] =
      p._2 match
        case str: DStr =>
          Pages.findPage(str.str).map(p._1 -> _.permalink)
        case _ => None
    configs.extractOrElse("hyperlinks")(MObj()).flatMap(f).toMap

  private inline def convert: String =
    if shouldConvert then convert(mainMatter)
    else mainMatter

  /** Convert the contents of the post to HTML */
  protected def render(up: IObj): String =
    val str = convert

    val c = MObj(postUrls.toList: _*) update
      MObj(
        "site" -> globals,
        "page" -> locals,
        "collectionItems" -> CollectionItems.collectionItems,
        "trees" -> treeObj
      )
    val context = IObj(
      PostHooks.beforeRenders(globals)(IObj(c)) update up
    )
    val r = render(str, context)
    PostHooks.afterRenders(globals)(context, r)

  /** Return the global settings for the collection-type treeType
    */
  def getTreesList(treeType: String): Data =
    configs.extractOrElse(treeType)(DNull)

  /** Write the post and run all the AfterWrite hooks */
  override def write(dryRun: Boolean): Unit =
    super.write(dryRun)
    PostHooks.afterWrites(globals)(this)

  /** Processes the groups in PostCluster this post belongs to. */
  if visible then PostForests.addToForests(this)

  override def toString(): String =
    CYAN(title) + s"($date)" + "[" + BLUE(permalink) + "]"

/** Constructor for PostLike objects */
object PostConstructor extends ElemConstructor:

  val styleName = "post"

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: IObj,
      collection: IObj
  ): Element =
    new PostLike(rType)(
      parentDir,
      relativePath,
      globals,
      collection
    )
