package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.plugins.Plugin

/** Trait to define a new style of elements. Some predefined examples are
  *   - PostLike: represents posts,
  *   - PageLike: represents static or generated pages, and
  *   - ItemLike: represents items that will be rendered as part of other pages
  *
  * This is a subclass of a Plugin, so new styles can be provided as plugins.
  *
  * There are two methods one need to implement.
  *   - `styleName` defines the name of this new style of Elements
  *   - `apply` defines how a new Element of this style will be created
  */
trait ElemConstructor extends Plugin:

  /** Name of this style of Elements */
  val styleName: String

  /** How to create a new Element of this style
    *
    * @param rType
    *   The named type of these Elements. For example, if the Collection is
    *   named "docs", then it's elements might have the type "doc"
    * @param parentDir
    *   The directory of this Element's Collection.
    * @param relativePath
    *   The relative path to the file of this Element from the `parentDir`
    * @param globals
    *   The global variables
    * @param collection
    *   The configuration variables for this Element's Collection
    *
    * @returns
    *   A new Element
    */
  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): Element
