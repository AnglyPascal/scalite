package com.anglypascal.scalite.collections

import scala.collection.mutable.LinkedHashMap

/** Trait to provide support for collections of things. Each collection can be
  * rendered to a new webpage with a list of all the posts. This can be toggled
  * in the template or in the global settings (see Jekyll blog). In the index
  * page, if page list is shown, there can be sections for collections.
  *
  * A collection of posts will be in a separate folder in the home directory,
  * and will be handled separately.
  */
trait Collection[A]:

  /** Name of the collection */
  val name: String

  /** Set of posts or other elements for use in context for rendering pages. */
  def things: Map[String, A]

  Collection.addToCollection(this)

/** TODO: The collection name need to be specified in the _config.yml :
  *
  * collection:
  *   - collection_name
  *
  * If the folder name is collection_name, then the content of that collection
  * will belong to _collection_name. If needed to specify collection data, used
  * as global while rendering the collection:
  *
  * collection: collection_name: tag: hello
  *
  * These collection names will be moved to the global data under the entry
  * "collection_name"
  */

/** Companion object holding set of all collections this site will render. Each
  * Collection itself is a companion object or something else of another class
  * that defines the behaviour of the elemnts of this collection. Posts is a
  * predefined example of this. DraftsPost is also defined and can be turned on
  * by adding collections: drafts: true option.
  */
object Collection:

  private val collections = LinkedHashMap[String, Collection[_]]()

  def addToCollection(col: Collection[_]) = collections += (col.name -> col)

/** Extra collections can be put in a separate folder defined by
  * collections_dir, which is by default base_dir
  *
  * they items inside a collection will be under _collection_name folder inside
  * the collections_dir. They will be defined by a generic handler like post,
  * but if provided a custom handler by inserting a Collection object with the
  * name inside collections, it will be rendered differently.
  *
  * Posts is rendered in this way. This is different from jekyll which handles
  * all the collections other than posts in the same way :DD
  *
  * figure out the custom tags that collections need to have
  */
