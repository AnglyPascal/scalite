package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Reader

/** Abstract class for a generic Item that simply has a source file to read from
  * and some internal variables defined in locals to process the contents of
  * that file with method render.
  */
trait Element extends Reader:

  /** local variales for this item */
  def locals: DObj

  /** Process the contents of this item */
  protected lazy val render: String

  /** Should the item be visible in the site? */
  lazy val visible: Boolean

type ElemConstructor = String => (String, String, DObj, DObj) => Element
