// package com.anglypascal.scalite

import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1._
import scala.collection.Map

class Layout(filename: String) 
extends HtmlReader(Globals.findLayout(filename).getOrElse(Globals.error)){

  val mustache = new Mustache(toHTML)
  private val parent: Option[Layout] = 
    if (data.obj.contains("layout"))
      Some(new Layout(data("layout").str))
    else
      None

  /** figure out the partials mechanism */
  def render(context: Obj, partials: Map[String, Layout] = Map()): String = {
    val newPartials = partials.map[String, Mustache]( p  => (p._1, p._2.mustache) )
    parent match {
      case None => mustache.render(context, newPartials)
      case Some(layout) => {
        val childString = mustache.render(context, newPartials); 
        context("content") = childString
        layout.render(context, partials)
      }
    }
  }
}

