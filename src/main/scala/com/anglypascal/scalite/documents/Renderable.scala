package com.anglypascal.scalite.documents

import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.layouts.Layouts

/** Defines the trait for objects that can be rendered. Each Renderable object
  * can have a parent layout and can be rendered to a HTML string.
  */
trait Renderable:

  /** The parent layout, might be None */
  protected lazy val layout =
    Layouts.get(layoutName) orElse Layouts.get("empty")

  /** Name of the parent layout */
  protected lazy val layoutName: String

  /** An Element also has some internal variables that are publicly visible */
  lazy val locals: DObj

  /** Should this object be visible to the reset of the site? */
  val visible: Boolean

  /** Renders the contents of this page, using the template in the parent
    * layout, if it exists, and returns a HTML string.
    */
  protected lazy val render: String
