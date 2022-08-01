package com.anglypascal.scalite

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
