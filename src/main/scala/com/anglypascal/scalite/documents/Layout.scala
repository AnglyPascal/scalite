package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DObj

/** users might want to define layouts for other templating language. to support
  * that, I need to
  *
  *   - give an api for layout creation
  *   - write an immutable wrapper around obj which will be passed to the
  *     layouts for rendering.
  */

abstract class Layout(val name: String, layoutPath: String)
    extends Reader(layoutPath):

  /** render function */
  def render(context: DObj, partials: Map[String, Layout]): String

  def parent: Option[Layout]

  def setParent(layouts: Map[String, Layout]): Unit

  def matches: Boolean = ???
  // this will tell whether this filetype is compatible with this renderer.
