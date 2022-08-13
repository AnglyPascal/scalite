package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.{DObj, DArr, DStr, DBool}

import scala.collection.mutable.LinkedHashMap
import com.typesafe.scalalogging.Logger

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  */
object Collections:

  /** Map of predefined collections that will later be populated "\_config.yml"
    */
  private val collections = LinkedHashMap[String, Collection[_]]()

  def addToCollection(col: Collection[_]) = collections += (col.name -> col)

  private val logger = Logger("Collection object")

  /** Processes all the collections that are set to output, with posts by
    * default.
    * @param collectionsDir
    *   the root collection directory. All collections must be in this directory
    * @param collectionData
    *   collection section from "\_config.yml"
    * @param globals
    *   global parameters
    */
  def apply(collectionsDir: String, collectionData: DObj, globals: DObj): Unit =

    val colsDir = collectionData.get("collectionsDir") match
      case Some(v): Some[DStr] => v.str
      case _                   => collectionsDir

    for key <- collectionData.keys if key != "collectionsDir" do
      val Col =
        if collections.contains(key) then
          logger.debug(s"found predefined collection object for $key")
          collections(key)
        else
          logger.debug(s"created new collection object for $key")
          new GenericItems(key)

      collectionData(key) match
        case cobj: DObj =>
          val output =
            if cobj.contains("output") then
              cobj("output") match
                case b: DBool => true
                case _        => false
            else if key == "posts" then
              logger.debug(s"posts are rendered by default")
              true
            else
              logger.debug(s"non posts collections are hidden by default: $key")
              false

          if !output then
            logger.debug(s"output set to false, won't process collection $key")
            collections.remove(key)
          else
            /** here add all the remaining into a stuff and ship with globals */
            val defKeys = List("output", "folder", "directory", "sortBy", "toc")
            val locals = cobj.remove(defKeys)

            val folder = cobj.get("folder") match
              case Some(d): Some[DStr] => d.str
              case _                   => s"/_$key"
            val dir = cobj.get("directory") match
              case Some(d): Some[DStr] => d.str + "/" + folder
              case _                   => colsDir + s"/_$key"
            logger.debug(s"fetching files from $dir for collection $key")

            val sortBy = cobj.get("sortBy") match
              case Some(d): Some[DStr] => Some(d.str)
              case _                   => None
            val toc = cobj.get("toc") match
              case Some(d): Some[DBool] => Some(d.bool)
              case _                    => None

            sortBy match
              case Some(s) =>
                toc match
                  case Some(t) =>
                    logger.debug(
                      s"found custom sortBy $s and toc $t for collection $key"
                    )
                    Col(dir, locals, globals, s, t)
                  case None =>
                    logger.debug(s"found custom sortBy $s for collection $key")
                    Col(dir, locals, globals, _sortBy = s)
              case None =>
                toc match
                  case Some(t) =>
                    logger.debug(s"found custom toc $t for collection $key")
                    Col(dir, locals, globals, _toc = t)
                  case None =>
                    logger.debug(
                      s"using default sortBy and toc for collection $key"
                    )
                    Col(dir, locals, globals)

        case cbool: DBool if cbool.bool =>
          logger.debug(s"rendering the collection $key")
          val dir = colsDir + s"/_$key"
          Col(dir, DObj(), globals)
        case cbool: DBool if !cbool.bool =>
          logger.debug(s"won't process the collection $key")
          collections.remove(key)
        case _ =>
          logger.debug(s"provide the metadata in a table or boolean for $key")
          collections.remove(key)

    // If posts haven't been explicitely configured, render it by default
    if collections.contains("posts") && !collectionData.contains("posts") then
      logger.debug("posts are being renderd by default")
      collections("posts")(colsDir + "/_posts", globals)
