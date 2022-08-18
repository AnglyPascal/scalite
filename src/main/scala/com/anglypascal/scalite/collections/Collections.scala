package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DBool
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
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

  def addToCollection(col: Collection[?]): Unit =
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
  def apply(collectionsDir: String, collectionData: DObj, globals: DObj): Unit =
    import com.anglypascal.scalite.data.DataExtensions.getOrElse
    import com.anglypascal.scalite.data.DataExtensions.extractOrElse

    // override the collectionsDir if it's in collectionData
    val colsDir = collectionData.getOrElse("collectionsDir")(collectionsDir)

    // create the collection named "key" for each key in collecionsDir
    for key <- collectionData.keys if key != "collectionsDir" do
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
        case cbool: DBool if cbool.bool =>
          logger.debug(s"rendering the collection $key")
          val dir = colsDir + s"/_$key"
          Col(dir, DObj(), globals)
          addToCollection(Col)

        // collections:
        //     drafts: false
        case cbool: DBool if !cbool.bool =>
          logger.debug(s"won't process the collection $key")
          collections.remove(key)

        // full configuration
        case cobj: DObj =>
          val output =
            if cobj.contains("output") then
              cobj("output") match
                case b: DBool => true
                case _        => false
            else if key == "posts" then
              logger.debug("posts are rendered by default")
              true
            else
              logger.debug(s"non posts collections are hidden by default: $key")
              false

          if !output then
            logger.debug(s"output set to false, won't process collection $key")
            collections.remove(key)
          else
            /** here add all the remaining into a stuff and ship with globals */
            val defKeys = List("output", "directory", "sortBy", "toc")
            val locals = cobj.removeAll(defKeys)

            val dir = cobj.getOrElse("directory")(colsDir + s"/_$key")
            logger.debug(s"fetching files from $dir for collection $key")

            val sortBy = cobj.getOrElse("sortBy")("title")
            val toc = cobj.getOrElse("sortBy")(false)
            val permalink = cobj.getOrElse("permalink")("")

            logger.debug(
              s"sorting by $sortBy, toc $toc, permalink $permalink for $key"
            )
            Col(dir, locals, globals, sortBy, toc, permalink)
            // add this collection to the collections map
            addToCollection(Col)

        // wasn't mentioned in the configuration
        case _ =>
          logger.debug(s"provide the metadata in a table or boolean for $key")
          collections.remove(key)

    // If posts haven't been explicitely configured, render it by default
    if !collectionData.contains("posts") then
      if !collections.contains("posts") then collections("posts") = Posts
      logger.debug("posts are being renderd by default")
      collections("posts")(colsDir + "/_posts", globals)
