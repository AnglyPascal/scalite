package com.anglypascal.scalite.documents

import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1.Obj

/** Layout represents a mustache template, that is rendered with it's parent,
  * the partials from the _includes folder, and the contents from either the
  * post or a child layout from the context.
  *
  * TODO: Layouts might have locally specified theme
  *
  * TODO: FATAL Problem, the render function of Layout and the render for Post
  * are totally different in function :(
  */
class Layout(filename: String, globals: Obj)
    extends Document(filename, globals):

  /** The mustache object for this layout */
  lazy val mustache = new Mustache(main_matter)

  /** Evaluate the template by rendering it with it's context and partials */
  def render(context: Obj, partials: Map[String, Layout]): String =
    val p = partials.map((s, l) => (s, l.mustache))
    mustache.render(context, p)

  def render(partials: Map[String, Layout]): String =
    val p = partials.map((s, l) => (s, l.mustache))
    mustache.render(globals, p)

object Layout:
  def apply(filename: String, globals: Obj) = new Layout(filename, globals)
