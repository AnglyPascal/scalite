package com.anglypascal.scalite

import _root_.com.anglypascal.mustache.Mustache
import _root_.com.rallyhealth.weejson.v1.*

class Layout(filename: String) extends Document(filename):
  /** */
  lazy val mustache = new Mustache(content)

  /** do we need the partials to be Map[String, Layout]?
    */
  def eval(context: Obj, partials: Map[String, Layout]): String =
    val p = partials.map((s, l) => (s, l.mustache))
    mustache.render(context, p)
