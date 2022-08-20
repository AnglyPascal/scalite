package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DBool
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  */
object Collections:

  /** Map of predefined collections that will later be populated by
    * "\_config.yml"
    */
  private val collections = LinkedHashMap[String, Collection[?]]()

  def addCollection(col: Collection[?]): Unit =
    collections += (col.name -> col)

  private val logger = Logger("Collection object")

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
    val colsDir = collectionData.getOrElse("collectionsDir")(collectionsDir)

    // create the collection named "key" for each key in collecionsDir
    for key <- collectionData.obj.keys if key != "collectionsDir" do
      val Col =
        if collections.contains(key) then
          logger.debug(s"found predefined collection object for $key")
          collections(key)
        else
          logger.debug(s"created new collection object for $key")
          new GenericCollection(key)

      collectionData(key) match
        // collections:
        //     drafts: true
        case cbool: Bool if cbool.bool =>
          logger.debug(s"rendering the collection $key")
          val dir = colsDir + s"/_$key"
          Col.setup(dir, globals)
          addCollection(Col)

        // collections:
        //     drafts: false
        case cbool: Bool if !cbool.bool =>
          logger.debug(s"won't process the collection $key")
          collections.remove(key)

        // full configuration
        case cobj: Obj =>
          val output =
            if key != "posts" && key != "statics" then
              cobj.extractOrElse("output")(false)
            else if key == "posts" || key == "statics" then
              logger.debug("posts are rendered by default")
              cobj.extractOrElse("output")(true)
            else
              logger.debug(s"non posts collections are hidden by default: $key")
              false

          if !output then
            logger.debug(s"output set to false, won't process collection $key")
            collections.remove(key)
          else
            val dir = cobj.extractOrElse("directory")(colsDir + s"/_$key")
            logger.debug(s"fetching files from $dir for collection $key")

            val sortBy =
              cobj.extractOrElse("sortBy")(Defaults.Collection.sortBy)
            val toc = cobj.extractOrElse("sortBy")(Defaults.Collection.toc)
            val permalinkTemplate = cobj.extractOrElse("permalinkTemplate")(
              globals.getOrElse("permalinkTemplate")(Defaults.permalinkTemplate)
            ) // FIXME the same permalink issues

            logger.debug(
              s"sorting by $sortBy, toc $toc, permalink $permalinkTemplate for $key"
            )

            /** the variables that needs to be passed to the items */
            val _globals = globals.add(
              "collection" -> DObj(
                "name" -> DStr(key),
                "permalinkTemplate" -> DStr(permalinkTemplate)
              )
            )

            Col.setup(dir, _globals, sortBy, toc, permalinkTemplate, DObj(cobj))
            // add this collection to the collections map
            addCollection(Col)

        // wasn't mentioned in the configuration
        case _ =>
          logger.debug(s"provide the metadata in a table or boolean for $key")
          collections.remove(key)

    // If posts haven't been explicitely configured, render it by default
    if !collectionData.obj.contains("posts") then
      if !collections.contains("posts") then collections("posts") = Posts
      logger.debug("posts are being renderd by default")
      collections("posts").setup(colsDir + "/_posts", globals)

  /** Process all the collections */
  def process: Unit =
    for (_, col) <- collections do col.process()
