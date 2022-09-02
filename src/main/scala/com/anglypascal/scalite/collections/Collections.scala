package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters._
import com.anglypascal.scalite.Configurable

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  */
object Collections extends Configurable:

  val sectionName: String = "collections"

  /** Map of predefined collections that will later be populated by
    * "\_config.yml"
    */
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

  def addStyle(elemCons: ElemConstructor): Unit =
    styles += elemCons.styleName -> elemCons

  private val collections = ListBuffer[Collection]()

  def addCollection(col: Collection): Unit = collections += col

  private val logger = Logger(BLUE("Collections"))

  /** Processes all the collections that are set to output, with posts by
    * default.bakira kichu
    * @param collectionsDir
    *   the root collection directory. All collections must be in this directory
    * @param collectionData
    *   collection section from "\_config.yml"
    * @param globals
    *   global parameters
    */
  def apply(
      configs: MObj,
      globals: IObj
  ): Unit =
    import com.anglypascal.scalite.data.DataExtensions.getOrElse
    import com.anglypascal.scalite.data.DataExtensions.extractOrElse

    collectionsConfig.update(configs)

    // override the collectionsDir if it's in collectionData
    val colsDir = globals.getOrElse("collectionsDir")(
        Defaults.Directories.collectionsDir
      )

    // create the collection named "key" for each key in collecionsDir
    for key <- collectionsConfig.keys do
      collectionsConfig(key) match
        /** the collectionObj that comes in will be an Obj type */
        case cobj: MObj =>
          val style = cobj.extractOrElse("style")("item")
          val output =
            if key == "posts" || key == "statics" then
              cobj.extractOrElse("output")(true)
            else cobj.extractOrElse("output")(false)

          if !output then
            logger.debug(s"output of collection ${RED(key)} is set to false")
          else
            val lout = cobj.extractOrElse("layout")(key)

            val prn = cobj.extractOrElse("directory")(colsDir)
            val fld = cobj.extractOrElse("folder")(s"/_$key")
            val dir = prn + (if fld.startsWith("/") then fld else "/" + fld)
            logger.debug(s"fetching files from $dir for collection $key")

            val sortBy =
              cobj.extractOrElse("sortBy")(Defaults.Collection.sortBy)
            val toc = cobj.extractOrElse("sortBy")(Defaults.Collection.toc)
            val permalinkTemplate = cobj.extractOrElse("permalink")(
              globals.getOrElse("permalink")(Defaults.permalink)
            ) // FIXME the same permalink issues

            logger.debug(
              s"collection $key: ${GREEN(
                  s"sortBy: $sortBy, toc: $toc, permalink: $permalinkTemplate"
                )}"
            )

            val Col = Collection(styles(style), key, lout)(
              dir,
              globals,
              sortBy,
              toc,
              permalinkTemplate,
              IObj(cobj)
            )
            // add this collection to the collections map
            addCollection(Col)

        // wasn't mentioned in the configuration
        case _ =>
          logger.debug(s"provide the metadata in a table or boolean for $key")

  /** Process all the collections */
  def process(): Unit =
    for col <- collections.par do col.process()

  override def toString(): String =
    collections
      .map(v => RED(v.name) + YELLOW(" -> ") + v.toString)
      .mkString("\n")
