package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.hooks.CollectionHooks
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.typesafe.scalalogging.Logger

/** A Collection is a collection of Renderable objects with a SourceFile, that
  * is, a Collection is made up of similar objects that are read from source
  * files residing in the source directory of the site.
  *
  * For example, `posts`, `drafts`, `sass` are predefined Collections:
  *   - `posts` handles the default implementation of PostLike objects, these
  *     are the "posts" of a blog. This Collection first collects all the files
  *     inside `/_posts` folder (or the reassigned folder), converts them using
  *     avaiable Converters, then renders them with available Layouts, finally
  *     writing them on the target destination.
  *   - `drafts` defines what it sounds like, draft pages and posts. By default
  *     draft posts and pages are not written to the destination.
  *   - `sass` collection handles the conversion of .sass and .scss files into
  *     .css files
  *
  * Another default use of Collection is the `statics` collection, which handles
  * all the static pages like index.html, about.html etc.
  *
  * The available Collections can be edited via the global configs file like so:
  * ```
  * collections:
  *   posts:
  *     folder: /posts # reassigns the directory
  *     sortBy: title # changes how posts are sorted in the table of contents
  *   drafts:
  *     output: true # marks the defaults to be added to the site
  *     permalink: "/drafts/{{date}}_{{title}}" # changes the permalink
  *   docs: # creates a new collection
  *     folder: /docs # reassigns the directory from the default /_docs
  *     layout: docs # defines the Layout to be used for the Elements
  *     style: post # the Elements should be PostLike
  * ```
  *
  * @param elemCons
  *   defines how elements of this collection should be created
  * @param name
  *   Name of this Collection
  * @param directory
  *   Absolute path to the directory where this collection resides
  * @param configs
  *   configurations of this Collection
  * @param globals
  *   Global settings
  */
class Collection(
    private val elemCons: ElemConstructor,
    val name: String,
    private val directory: String,
    _configs: MObj,
    protected val globals: IObj
) extends Page:

  private val logger = Logger(s"${BLUE(name.capitalize)}")

  protected val configs: MObj =
    _configs update CollectionHooks.beforeInits(globals)(IObj(_configs))

  private lazy val sortBy =
    configs.remove("sortBy") match
      case None => Array("title")
      case Some(v) =>
        val arr = v match
          case v: com.anglypascal.scalite.data.mutable.DStr =>
            v.str.trim.split(",").map(_.trim)
          case v: com.anglypascal.scalite.data.mutable.DArr =>
            v.arr.flatMap(_.getStr).toArray
          case _ => Array[String]()
        if arr.length == 0 then Array("title")
        else arr

  private lazy val permalinkTemplate =
    extractChain(configs, globals)("permalink")(Defaults.permalink)

  protected lazy val layoutName: String = configs.extractOrElse("layout")(name)

  val visible: Boolean =
    configs.extractOrElse("toc")(Defaults.Collection.toc)

  logger.debug(
    s"Init: source: ${GREEN(directory)}, " +
      s"sortBy: ${GREEN(sortBy.mkString(", "))}, " +
      s"toc: ${GREEN(visible.toString)}, " +
      s"permalink: ${GREEN(permalinkTemplate)}"
  )

  /** Set of posts or other elements for use in context for rendering pages.
    *
    * FIXME: I don't like it being open
    */
  lazy val items: Map[String, Element] =
    lazy val constructor: (String, String, IObj, IObj) => Element =
      elemCons(name)

    val files = getListOfFilepaths(directory)
    logger.debug(s"$name source ${GREEN(directory)}: ${files.length} files")
    def f(fn: String) =
      (getFileName(fn), constructor(directory, fn, globals, locals))

    files.filter(Converters.hasConverter).map(f).toMap

  lazy val locals =
    val scopedDefaults = ScopedDefaults.getDefaults(name, "collection")
    configs update scopedDefaults

    configs += "title" -> name
    configs update CollectionHooks.beforeLocals(globals)(IObj(configs))

    IObj(configs)

  lazy val identifier = s"/collections/$name"

  lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  def sortedItems: Array[Element] =
    def compare(fst: Element, snd: Element): Boolean =
      compareBy(fst, snd, sortBy: _*) < 0

    items
      .collect(p =>
        p._2.visible match
          case true => p._2
      )
      .toArray
      .sortWith(compare)

  /** The toc will have default output extension html */
  protected lazy val outputExt = locals.getOrElse("outputExt")(".html")

  protected lazy val render: String =
    val str = layout match
      case None => ""
      case Some(p) =>
        val c = MObj(
          "site" -> globals,
          "page" -> locals,
          "items" -> DArr(sortedItems.map(_.locals))
        )
        c update CollectionHooks.beforeRenders(globals)(IObj(c))
        p.renderWrap(IObj(c))
    CollectionHooks.afterRenders(globals)(locals, str)

  /** This sorts out the items, renders them, and writes them to the disk */
  protected[collections] def process(dryrun: Boolean = false): Unit =
    import scala.collection.parallel.CollectionConverters._
    for item <- items.values.par do
      item match
        case item: Page => item.write(dryrun)
        case _          => ()
    write(dryrun)
    CollectionHooks.afterWrites(globals)(this)

  /** TODO: Add caching options */
  protected[collections] def cache(): Unit = ???

  override def toString(): String =
    s"${GREEN(name)} collection: \n" +
      sortedItems.map("  " + _.toString).mkString("\n")

  /** Compare two given Elements by the given keys */
  private def compareBy(
      fst: Element,
      snd: Element,
      keys: String*
  ): Int =
    import com.anglypascal.scalite.utils.cmpOpt
    var s = 0
    for key <- keys if s == 0 do
      s += cmpOpt(fst.locals.get(key), snd.locals.get(key))
    if s == 0 then cmpOpt(fst.locals.get("title"), snd.locals.get("title"))
    else s
