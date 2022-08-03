package com.anglypascal.scalite

import com.anglypascal.scalite.documents.Page
import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1.Obj

case class URL(str: String):
  /** */
  val template = new Mustache(str)

  def apply(placeholders: Obj) =
    template.render(placeholders, partials)

  private val partials: Map[String, Mustache] =
    Map(
      "date" -> "/{{categories}}/{{year}}/{{month}}/{{day}}/{{title}}{{output_ext}}",
      "pretty" -> "/{{categories}}/{{year}}/{{month}}/{{day}}/{{title}}",
      "ordinal" -> "/{{categories}}/{{year}}/{{y_day}}/{{title}}{{output_ext}}",
      "weekdate" -> "/{{categories}}/{{year}}/W{{week}}/{{short_day}}/{{title}}{{output_ext}}",
      "none" -> "/{{categories}}/{{title}}{{output_ext}}"
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
