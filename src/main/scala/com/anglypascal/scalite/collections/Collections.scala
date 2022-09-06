package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DataExtensions.extractChain
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DBool
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters._
import com.anglypascal.scalite.documents.Generator

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
    MObj(
      "posts" -> MObj(
        "output" -> Posts.output,
        "folder" -> Posts.folder,
        "name" -> Posts.name,
        "directory" -> Posts.directory,
        "sortBy" -> Posts.sortBy,
        "toc" -> Posts.toc,
        "permalink" -> Posts.permalink,
        "layout" -> Posts.layout,
        "style" -> Posts.style
      ),
      "drafts" -> MObj(
        "output" -> Drafts.output,
        "folder" -> Drafts.folder,
        "name" -> Drafts.name,
        "directory" -> Drafts.directory,
        "sortBy" -> Drafts.sortBy,
        "toc" -> Drafts.toc,
        "permalink" -> Drafts.permalink,
        "layout" -> Drafts.layout,
        "style" -> Drafts.style
      ),
      "statics" -> MObj(
        "output" -> Statics.output,
        "folder" -> Statics.folder,
        "name" -> Statics.name,
        "directory" -> Statics.directory,
        "sortBy" -> Statics.sortBy,
        "toc" -> Statics.toc,
        "permalink" -> Statics.permalink,
        "layout" -> Statics.layout,
        "style" -> Statics.style
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
    for colName <- collectionsConfig.keys do
      collectionsConfig(colName) match
        /** the collectionObj that comes in will be an Obj type */
        case cobj: MObj =>
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
