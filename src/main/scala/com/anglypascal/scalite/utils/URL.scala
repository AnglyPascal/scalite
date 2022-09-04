package com.anglypascal.scalite

import com.anglypascal.mustache.Mustache
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.layouts.DataAST

object URL:

  DataAST.init()

  /** Given the template str and the placeholders, render the template to create
    * a relative permanent link
    */
  def apply(str: String)(placeholders: DObj) =
    val template = new Mustache(str)
    template.render(placeholders, partials)

  /** Predefined partials to be used with the permalink templates */
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
