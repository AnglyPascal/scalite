package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DBool
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Generator
import com.anglypascal.scalite.hooks.CollectionHooks
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

  /** Add a new ElemConstructor */
  def addStyle(elemCons: ElemConstructor): Unit =
    styles += elemCons.styleName -> elemCons

  /** Defaults of the `collection` section. */
  private def defaultConfigs: MObj =
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

  private val collections = ListBuffer[Collection]()

  def pages = collections.toList

  /** Gets the configuration set in the "collections" section of `_configs.yml`
    * and creates necessary Collection objects
    */
  def apply(_configs: MObj, globals: IObj): Unit =
    for (key, value) <- _configs do
      value match
        case v: DBool => _configs(key) = MObj("output" -> v.bool)
        case _        => ()

    val configs = defaultConfigs update _configs

    val base = globals.getOrElse("base")(Defaults.Directories.base)
    val colsDir =
      globals.getOrElse("collectionsDir")(Defaults.Directories.collectionsDir)

    // create the collection named "name" for each name in collecionsDir
    for (name, config) <- configs do
      config match
        case config: MObj =>
          val style = config.extractOrElse("style")("item")
          val output =
            if name == "posts" || name == "statics" then
              config.extractOrElse("output")(true)
            else config.extractOrElse("output")(false)

          if !output then logger.debug(s"won't output ${RED(name)}")
          else
            val parentDir = config.extractOrElse("directory")(colsDir)
            val folder = config.extractOrElse("folder")(s"/_$name")
            val dir = base + parentDir +
              (if folder.startsWith("/") then "" else "/") + folder

            val Col = Collection(styles(style), name, dir, configs, globals)

            // If this collection has style "item" then add its elements to the
            // CollectionItems object
            if style == "item" then CollectionItems.addItems(name, Col.items)
            // add this collection to the collections map
            collections += Col

        // wasn't mentioned in the configuration
        case _ =>
          logger.error(
            s"${RED(name)}: provide collection metadata in a table or a boolean"
          )

  /** Process all the collections */
  def process(dryRun: Boolean = false): Unit =
    for col <- collections.par do col.process(dryRun)

  override def toString(): String =
    collections
      .map(v => MAGENTA(v.name) + YELLOW(": ") + v.toString)
      .mkString("\n")

  /** Cleans up the collections to start anew */
  protected[scalite] def reset(): Unit =
    styles.clear()
    styles ++= LinkedHashMap[String, ElemConstructor](
      "post" -> PostConstructor,
      "page" -> PageConstructor,
      "item" -> ItemConstructor
    )
    collections.clear()
    CollectionItems.reset()

/** TODO: how will other objects access these? These should be available to
  * objects at render time inside context.
  */
object CollectionItems:

  private val _allItems = LinkedHashMap[String, Map[String, Element]]()

  lazy val collectionItems =
    println("haha")
    val i =  IObj(
      _allItems.map(p => (p._1, IObj(p._2.map(t => (t._1, t._2.locals))))).toMap
    )
    println(i)
    i


  def addItems(colName: String, items: Map[String, Element]): Unit =
    _allItems += colName -> items

  protected[collections] def reset(): Unit = _allItems.clear()
