package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DBool
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Generator
import com.anglypascal.scalite.plugins.CollectionHooks
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters._

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  *
  * It's a Configurable, so it looks out for the "collections" section in
  * \_config.yml to configure itself.
  */
object Collections extends Configurable with Generator:

  private val logger = Logger(BLUE("Collections"))

  val sectionName: String = "collections"

  /** Avaiable Element styles */
  private val styles = LinkedHashMap[String, ElemConstructor](
    "post" -> PostConstructor,
    "page" -> PageConstructor,
    "item" -> ItemConstructor
  )

  /** Defaults of the `collection` section. */
  private lazy val collectionsConfig =
    import Defaults.Posts
    import Defaults.Drafts
    import Defaults.Statics
    import Defaults.Sass
    MObj(
      "posts" -> MObj(
        "directory" -> Posts.directory,
        "folder" -> Posts.folder,
        "layout" -> Posts.layout,
        "name" -> Posts.name,
        "output" -> Posts.output,
        "permalink" -> Posts.permalink,
        "sortBy" -> Posts.sortBy,
        "style" -> Posts.style,
        "toc" -> Posts.toc
      ),
      "drafts" -> MObj(
        "directory" -> Drafts.directory,
        "folder" -> Drafts.folder,
        "layout" -> Drafts.layout,
        "name" -> Drafts.name,
        "output" -> Drafts.output,
        "permalink" -> Drafts.permalink,
        "sortBy" -> Drafts.sortBy,
        "style" -> Drafts.style,
        "toc" -> Drafts.toc
      ),
      "statics" -> MObj(
        "directory" -> Statics.directory,
        "folder" -> Statics.folder,
        "layout" -> Statics.layout,
        "name" -> Statics.name,
        "output" -> Statics.output,
        "permalink" -> Statics.permalink,
        "sortBy" -> Statics.sortBy,
        "style" -> Statics.style,
        "toc" -> Statics.toc
      ),
      "sass" -> MObj(
        "directory" -> Sass.directory,
        "folder" -> Sass.folder,
        "layout" -> Sass.layout,
        "name" -> Sass.name,
        "output" -> Sass.output,
        "sortBy" -> Sass.sortBy,
        "permalink" -> Sass.permalink,
        "style" -> Sass.style,
        "toc" -> Sass.toc
      )
    )

  private def defaultConf(bool: Boolean): MObj =
    MObj("output" -> bool)

  /** Add a new ElemConstructor */
  def addStyle(elemCons: ElemConstructor): Unit =
    styles += elemCons.styleName -> elemCons

  private val collections = ListBuffer[Collection]()
  def pages = collections.toList

  /** Gets the configuration set in the "collections" section of \_configs.yml
    * and creates necessary Collection objects
    */
  def apply(
      configs: MObj,
      globals: IObj
  ): Unit =
    for (key, value) <- configs do
      value match
        case v: DBool =>
          configs(key) = defaultConf(v.bool)
        case _ => ()

    collectionsConfig update configs

    val base = globals.getOrElse("base")(Defaults.Directories.base)
    val colsDir =
      globals.getOrElse("collectionsDir")(Defaults.Directories.collectionsDir)

    // create the collection named "key" for each key in collecionsDir
    for (colName, c) <- collectionsConfig do
      c match
        case c: MObj =>
          // add all the updates from the CollectionHooks, with higher priority ones
          // applied before the lower priority ones
          val cobj = CollectionHooks.beforeInits
            .foldLeft(c)((o, h) => o update h.apply(globals)(IObj(o)))

          val style = cobj.extractOrElse("style")("item")
          val output =
            if colName == "posts" || colName == "statics" then
              cobj.extractOrElse("output")(true)
            else cobj.extractOrElse("output")(false)

          if !output then logger.debug(s"won't output ${RED(colName)}")
          else
            val layout = cobj.extractOrElse("layout")(colName)

            val prn = cobj.extractOrElse("directory")(colsDir)
            val fld = cobj.extractOrElse("folder")(s"/_$colName")
            val dir =
              base + prn + (if fld.startsWith("/") then fld else "/" + fld)

            logger.debug(s"${CYAN(colName)} source: ${GREEN(dir)}")

            val sortBy =
              cobj.extractOrElse("sortBy")(Defaults.Collection.sortBy)
            val toc = cobj.extractOrElse("sortBy")(Defaults.Collection.toc)
            val permalinkTemplate =
              extractChain(cobj, globals)("permalink")(Defaults.permalink)

            logger.debug(
              s"${CYAN(colName)}: " +
                s"sortBy: ${GREEN(sortBy)}, toc: ${GREEN(toc.toString)}, " +
                s"permalink: ${GREEN(permalinkTemplate)}"
            )

            val Col = Collection(styles(style), colName, layout)(
              dir,
              globals,
              sortBy,
              toc,
              permalinkTemplate,
              cobj
            )

            // If this collection has style "item" then add its elements to the
            // CollectionItems object
            if style == "item" then CollectionItems.addItems(colName, Col.items)
            // add this collection to the collections map
            collections += Col

        // wasn't mentioned in the configuration
        case _ =>
          logger.error(
            s"${RED(colName)}: provide collection metadata in a table or a boolean"
          )

  /** Process all the collections */
  def process(dryRun: Boolean = false): Unit =
    for col <- collections.par do col.process(dryRun)

  override def toString(): String =
    collections
      .map(v => MAGENTA(v.name) + YELLOW(": ") + v.toString)
      .mkString("\n")

/** TODO: how will other objects access these? These should be available to
  * objects at render time inside context.
  */
object CollectionItems:

  private val _allItems = LinkedHashMap[String, Map[String, Element]]()

  lazy val collectionItems = IObj(
    _allItems.map(p => (p._1, IObj(p._2.map(t => (t._1, t._2.locals))))).toMap
  )

  def addItems(colName: String, items: Map[String, Element]) =
    _allItems += colName -> items
