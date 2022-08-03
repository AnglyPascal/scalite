package com.anglypascal.scalite

import com.anglypascal.scalite.documents.Page
import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1.Obj

case class URL(str: String):
  /** */
  val template = new Mustache(str)

  def render(placeholders: Obj): String = template.render(placeholders)

  def apply(placeholders: Obj) = render(placeholders)

  /** TODO: Define the partials here, that are the abbreviations
   */

/** TODO: The permalink url will be given in form of an absolute link or a
  * mustache template with the placeholders from a list.
  *
  * https://jekyllrb.com/docs/permalinks/
  *
  * can be specified as a global permalink, where will also be a default, and
  * then individual posts might override the global setting.
  *
  * There are some rules that I need to flesh out still
  */

/** Where is the url creation done?
  *
  * I think it should be done by each page at render time, as the url will
  * depend on the local data
  */
