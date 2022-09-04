package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.DArr
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.typesafe.scalalogging.Logger

import scala.collection.parallel.CollectionConverters._

/** Defines a new Collection of Element's. Responsible for fetching the Elements
  * from the directory and processing them into HTML pages.
  *
  * It's a Page, so can be rendered into a tableOfContents page using the layout
  * with the given layout name.
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
    private val globals: DObj,
    private val sortBy: String,
    val visible: Boolean,
    private val permalinkTemplate: String,
    val locals: DObj
) extends Page:

  private val logger = Logger(s"${BLUE(name.capitalize)}")

  /** Set of posts or other elements for use in context for rendering pages. */
  lazy val items =
    val files = getListOfFilepaths(directory)
    logger.debug(s"$name source ${GREEN(directory)}: ${files.length} files")
    def f(fn: String) =
      (getFileName(fn), constructor(directory, fn, globals, locals))
    files.filter(Converters.hasConverter).map(f).toMap

  private var constructor = elemCons(name)

  lazy val identifier = s"/collections/$name"

  lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  /** TODO why is there an @uncheckedVariance annotation? */
  private def sortedItems =
    val v = items.map(_._2).filter(_.visible).toArray.sortWith(compare)
    v

  /** The toc will have default output extension html */
  protected lazy val outputExt = locals.getOrElse("outputExt")(".html")

  /** The compare function to be used with sortWith to sort the posts in this
    * collection. This first tries sortBy then falls back to "title".
    */
  private def compare(fst: Element, snd: Element): Boolean =
    compareBy(fst, snd, sortBy) < 0

  protected lazy val render: String =
    layout match
      case None => ""
      case Some(p) =>
        val context = DObj(
          "site" -> globals,
          "page" -> locals.add("title" -> DStr(name)),
          "items" -> DArr(sortedItems.map(_.locals))
        )
        p.render(context)

  /** This sorts out the items, renders them, and writes them to the disk */
  protected[collections] def process(dryrun: Boolean = false): Unit =
    for (_, item) <- items.par do
      item match
        case item: Page => item.write(dryrun)
        case _          => ()
    write(dryrun)

  /** TODO: Add caching options */
  protected[collections] def cache(): Unit = ???

  override def toString(): String =
    "\n" + sortedItems.map("  " + _.toString).mkString("\n")
