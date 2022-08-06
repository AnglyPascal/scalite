package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName}

import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1.{Obj, Str}
import scala.collection.mutable.LinkedHashMap

/** Layout represents a mustache template, that is rendered with it's parent,
  * the partials from the _includes folder, and the contents from either the
  * post or a child layout from the context.
  *
  * TODO: Layouts might have locally specified theme
  */
class Layout(val name: String, layoutPath: String) extends Reader(layoutPath):
  /** */
  private var _parent: Option[Layout] = None
  def parent = _parent

  def setParent(layouts: Map[String, Layout]): Unit =
    _parent =
      if front_matter.obj.contains("layout") then
        front_matter("layout") match
          case s: Str =>
            val pn = s.str
            layouts.get(pn)
          case _ => None
      else None

  /** The mustache object for this layout */
  lazy val mustache = new Mustache(main_matter)

  /** Evaluate the template by rendering it with it's context and partials */
  def render(context: Obj, partials: Map[String, Layout]): String =
    val p = partials.map((s, l) => (s, l.mustache))
    val str = mustache.render(context, p)
    parent match
      case Some(p) =>
        context("content") = str
        p.render(context, partials)
      case _ => str

/** Companion object
  */
object Layout:

  /** prevents the call of layouts before doing the apply
    */
  private var _layouts: Map[String, Layout] = _
  def layouts = _layouts

  def apply(directory: String): Map[String, Layout] =
    val files = getListOfFiles(directory)
    val ls = files
      .map(f => {
        val fn = getFileName(f)
        (fn, new Layout(getFileName(f), f))
      })
      .toMap

    ls.map((s, l) => l.setParent(ls))
    _layouts = ls.toMap
    layouts
