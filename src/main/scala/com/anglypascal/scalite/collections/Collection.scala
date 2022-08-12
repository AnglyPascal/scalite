package com.anglypascal.scalite.collections

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.utils.DObj
import com.anglypascal.scalite.utils.DArr
import com.anglypascal.scalite.utils.DStr
import com.anglypascal.scalite.utils.DBool
import com.typesafe.scalalogging.Logger

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  */
trait Collection[A]: // TODO: need to supply some metadata

  /** Name of the collection */
  val name: String

  /** Set of posts or other elements for use in context for rendering pages. */
  def things: Map[String, A]

  /** This sorts out the items, renders them, and writes them to the disk */
  def render: Unit

  def apply(directory: String, globals: DObj): Map[String, A]

  // val pageOfCollection: Boolean

  Collection.addToCollection(this)

/** Companion object with set of collections this site has. Each collection has
  * a name, a list of items, and a method to render the items and if specified,
  * a table of contents like page for the collction.
  *
  * Posts are a predefined example of a collection. Users can specify custom
  * collections under the collections section in the "\_config.yml" file like:
  * {{{
  * collections:
  *   col-name:
  *     output: true
  *     folder: col_folder
  *     directory: /path/to/custom/collection
  *     toc: true
  *     ... anything else this collection specific
  * }}}
  * By default all items of a collection other than posts will be handled by the
  * [[GenericItem]] class. To change this behavior, custom Collection objects
  * can be provided.
  */
object Collection:

  /** Map of predefined collections that will later be populated "\_config.yml"
    */
  private val collections = LinkedHashMap[String, Collection[_]]()

  def addToCollection(col: Collection[_]) = collections += (col.name -> col)

  private val logger = Logger("Collection object")

  /** Processes all the collections that are set to output, with posts by
    * default.
    * @param colDir
    *   the root collection directory. All collections must be in this directory
    * @param colData
    *   collection section from "\_config.yml"
    * @param globals
    *   global parameters
    */
  def apply(colDir: String, colData: DObj, globals: DObj): Unit =
    for (key <- colData.obj.keys) do
      val Col =
        if collections.contains(key) then
          logger.debug(s"found predefined collection object for $key")
          collections(key)
        else
          logger.debug(s"created new collection object for $key")
          new GenericCollection(key)

      colData(key) match
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
            logger.debug(
              s"since output is set to false, collection $key will not be processed"
            )
            collections.remove(key)
          else
            val folder = cobj.get("folder") match
              case Some(d): Some[DStr] => d.str
              case _                   => s"/_$key"
            val dir = cobj.get("directory") match
              case Some(d): Some[DStr] => d.str + "/" + folder
              case _                   => colDir + s"/_$key"
            Col(dir, globals)
        case cbool: DBool =>
          val dir = colDir + s"/_$key"
          Col(dir, globals)
        case _ =>
          logger.debug(
            s"please provide the metadata in a table or boolean for $key"
          )
          collections.remove(key)

    if collections.contains("posts") then
      logger.debug("posts are being renderd by default")
      collections("posts")(colDir + "/_posts", globals)

    /** TODO: Specify in if the colletion should have a toc-like page. One way
      * to achieve this is to pass in whatever remaining data is in colData to
      * the collection.
      *
      *   - sort-by
      */
