package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.plugins.CollectionHooks
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.typesafe.scalalogging.Logger

import scala.collection.parallel.CollectionConverters._

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
  *   An ElemConstructor, defines how elements of this collection should be
  *   created
  * @param name
  *   Name of this Collection
  * @param layoutName
  *   Name of the layout of this Collection Page
  * @param directory
  *   Absolute path to the directory where this collection resides
  * @param globals
  *   Global settings
  * @param sortBy
  *   How should the elements of this Collection be sorted
  * @param visible
  *   Should this Collection be made into a Page?
  * @param permalinkTemplate
  *   The template for the permalink of this Page
  * @param locals
  *   Local variables set in the collections.name section in /_configs.yml
  */
class Collection(
    private val elemCons: ElemConstructor,
    val name: String,
    protected val layoutName: String
)(
    private val directory: String,
    protected val globals: IObj,
    private val sortBy: String,
    val visible: Boolean,
    private val permalinkTemplate: String,
    protected val configs: MObj
) extends Page:

  private val logger = Logger(s"${BLUE(name.capitalize)}")

  /** Set of posts or other elements for use in context for rendering pages. */
  lazy val items: Map[String, Element] =
    val files = getListOfFilepaths(directory)
    logger.debug(s"$name source ${GREEN(directory)}: ${files.length} files")
    def f(fn: String) =
      (getFileName(fn), constructor(directory, fn, globals, locals))
    files.filter(Converters.hasConverter).map(f).toMap

  lazy val locals =
    val scopedDefaults = ScopedDefaults.getDefaults(name, "collection")
    configs update scopedDefaults

    configs += "title" -> name
    val conf = CollectionHooks.beforeLocals
      .foldLeft(configs)((o, h) => o update h(globals)(IObj(o)))

    IObj(conf)

  private var constructor: (String, String, IObj, IObj) => Element =
    elemCons(name)

  lazy val identifier = s"/collections/$name"

  lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  private def sortedItems: Array[Element] =
    items
      .collect(p =>
        p._2.visible match
          case true => p._2
      )
      .toArray
      .sortWith(compare)

  /** The toc will have default output extension html */
  protected lazy val outputExt = locals.getOrElse("outputExt")(".html")

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  private def compare(fst: Element, snd: Element): Boolean =
    compareBy(fst, snd, sortBy) < 0

  protected lazy val render: String =
    val str = layout match
      case None => ""
      case Some(p) =>
        val c = MObj(
          "site" -> globals,
          "page" -> locals,
          "items" -> DArr(sortedItems.map(_.locals))
        )
        val con = CollectionHooks.beforeRenders
          .foldLeft(c)((o, h) => o update h(globals)(IObj(o)))
        p.renderWrap(IObj(con))
    CollectionHooks.afterRenders.foldLeft(str)((o, h) => h(globals)(locals, o))

  /** This sorts out the items, renders them, and writes them to the disk */
  protected[collections] def process(dryrun: Boolean = false): Unit =
    for item <- items.values.par do
      item match
        case item: Page => item.write(dryrun)
        case _          => ()
    write(dryrun)
    CollectionHooks.afterWrites foreach { _.apply(globals)(this) }

  /** TODO: Add caching options */
  protected[collections] def cache(): Unit = ???

  override def toString(): String =
    "\n" + sortedItems.map("  " + _.toString).mkString("\n")
