package com.anglypascal.scalite

import com.anglypascal.mustache.Mustache
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Page

case class URL(str: String):
  /** */
  val template = new Mustache(str)

  def apply(placeholders: DObj) =
    template.render(placeholders, partials)

  private val partials: Map[String, Mustache] =
    import com.anglypascal.scalite.Defaults.URLPartials.*
    Map(
      "date" -> date,
      "slugDate" -> slugDate,
      "pretty" -> pretty,
      "ordinal" -> ordinal,
      "weekdate" -> weekdate,
      "none" -> none
    ).map((s, m) => (s, new Mustache(m)))

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
