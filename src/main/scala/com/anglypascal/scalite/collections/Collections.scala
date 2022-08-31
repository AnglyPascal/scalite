package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DBool
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.utils.Colors.*
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters._

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  */
object Collections:
  /** Map of predefined collections that will later be populated by
    * "\_config.yml"
    */
  private val styles = LinkedHashMap[String, ElemConstructor](
    "post" -> PostConstructor,
    "page" -> PageConstructor,
    "item" -> ItemConstructor
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
  def apply(collectionsDir: String, collectionData: Obj, globals: DObj): Unit =
    import com.anglypascal.scalite.data.DataExtensions.getOrElse
    import com.anglypascal.scalite.data.DataExtensions.extractOrElse

    // override the collectionsDir if it's in collectionData
    val colsDir = collectionData.extractOrElse("collectionsDir")(collectionsDir)

    // create the collection named "key" for each key in collecionsDir
    for key <- collectionData.obj.keys do
      collectionData(key) match
        /** the collectionObj that comes in will be an Obj type */
        case cobj: Obj =>
          val style = cobj.extractOrElse("style")("item")
          val output =
            if key != "posts" && key != "statics" then
              cobj.extractOrElse("output")(false)
            else if key == "posts" || key == "statics" then
              cobj.extractOrElse("output")(true)
            else false

          if !output then
            logger.debug(s"output of collection ${RED(key)} is set to false")
          else
            val lout = cobj.extractOrElse("layout")(key)

            val prn = cobj.extractOrElse("directory")(colsDir)
            val fld = cobj.extractOrElse("folder")(s"/_$key")
            val dir = prn + (if fld.startsWith("/") then fld else "/" + fld)
            logger.debug(s"fetching files from $dir for collection $key")

            val Col = new Collection(styles(style), key, lout)

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

            /** the variables that needs to be passed to the items */
            val _globals = globals.add(
              "collection" -> DObj(
                "name" -> DStr(key),
                "permalink" -> DStr(permalinkTemplate)
              )
            )

            Col.setup(dir, _globals, sortBy, toc, permalinkTemplate, DObj(cobj))
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
