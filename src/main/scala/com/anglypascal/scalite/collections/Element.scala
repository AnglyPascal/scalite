package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.Plugin

/** Abstract class for a generic Item that simply has a source file to read from
  * and some internal variables defined in locals to process the contents of
  * that file with method render.
  */
trait Element extends Reader with Renderable:

  /** local variales for this item */
  def locals: DObj

trait ElemConstructor extends Plugin:

  val styleName: String 

  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): Element
