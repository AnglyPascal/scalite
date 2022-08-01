package com.anglypascal.scalite

import com.rallyhealth.weejson.v1._

object Globals:
  val dirs = Map(
    "base_dir" -> "/src/main/scala/site_template",
    "layout_dir" -> "/_layouts",
    "post_dir" -> "/_posts",
    "static_dir" -> "/_includes",
    "sass_dir" -> "/_sass"
  )

  val site = Obj(
    "title" -> "site title",
    "lang" -> "en",
    "paginate" -> false,
    "show_excerpts" -> true,
    "root_url" -> "/",
    "author" -> Obj(
      "name" -> "author name",
      "email" -> "author email",
      ),
    "description" -> "site description",
    "tag_layout" -> "tag",
  )
