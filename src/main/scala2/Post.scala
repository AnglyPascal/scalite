// package com.anglypascal.scalite

import com.anglypascal.mustache.Mustache
import com.rallyhealth.weejson.v1._

class Post(filename: String) 
extends MarkdownReader(Globals.siteDir + "/" + filename){

  def date: String = "return date"

  val post = data
  post("url") = Str(
    if (post.obj.contains("permalink"))
      Globals.baseURL + post("permalink")
    else Globals.baseURL + post("title")
    )

  post("date") = date
  post("content") = raw"$toHTML"

  private val layoutName = 
    if (post.obj.contains("layout")) 
      post("layout").str
    else "post"

  private val layout = Globals.layouts(layoutName)
  private val obj = Obj("post" -> post, "site" -> Globals.siteObj)

  def render(): String = layout.render(obj, Globals.partials)
}
