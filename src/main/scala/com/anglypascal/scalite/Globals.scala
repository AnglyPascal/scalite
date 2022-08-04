package com.anglypascal.scalite

import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.readers.*
import com.anglypascal.scalite.utils.yamlParser
import com.anglypascal.scalite.bags.*

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str}
import scala.collection.mutable.LinkedHashMap

object Globals:
  val site = Obj(
    "base_dir" -> "/src/main/scala/site_template",
    "layout_dir" -> "/_layouts",
    "post_dir" -> "/_posts",
    "static_dir" -> "/_includes",
    "sass_dir" -> "/_sass",
    "title" -> "site title",
    "lang" -> "en",
    "paginate" -> false,
    "show_excerpts" -> true,
    "root_url" -> "/",
    "description" -> "site description",
    "tag_layout" -> "tag",
    "author" -> Obj(
      "name" -> "author name",
      "email" -> "author email"
    ),
    "date_format" -> "dd MMM, yyyy"
  )

  /** Support for data provided in _data folder. this will be in site("data") */
  private val config = yamlParser(site("base_dir").str + "/config.yml")
  for (key, value) <- config.obj do site(key) = value

  val layouts = Layout(site("base_dir").str + site("layout_dir").str)
  val partials = Partial(site("base_dir").str + site("static_dir").str)
  val statics = Post(site("base_dir").str, site)
  val posts = Post(site("base_dir").str + site("layout_dir").str, site)

  /** If I want to allow for collections, these things need to go to a different
    * class? And the variables should be extensible
    */
