package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str}
import com.anglypascal.scalite.readers.*
import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.collections.Tag

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
    )
  )

  private val config = yamlParser(site("base_dir").str + "/config.yml")
  for (key, value) <- config.obj do site(key) = value

  val tags = LinkedHashMap[String, Tag]()
  val layouts = LayoutsReader(site("base_dir").str + site("layout_dir").str)
  val partials = PartialsReader(site("base_dir").str + site("static_dir").str)
  val statics = PostsReader(site("base_dir").str, layouts, tags)
  val posts =
    PostsReader(site("base_dir").str + site("layout_dir").str, layouts, tags)

  /** If I want to allow for collections, these things need to go to a different
    * class? And the variables should be extensible
    */
