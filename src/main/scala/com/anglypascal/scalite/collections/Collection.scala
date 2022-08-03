package com.anglypascal.scalite.collections

import scala.collection.mutable.Set
import com.rallyhealth.weejson.v1.Obj

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
  val things: Set[A]

  /** Add a new thing to this collection */
  def add(a: A) = things += a

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
