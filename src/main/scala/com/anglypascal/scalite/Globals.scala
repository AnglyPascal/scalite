package com.anglypascal.scalite

import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.utils.yamlParser
import com.anglypascal.scalite.bags.*

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str}
import scala.collection.mutable.LinkedHashMap

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  */
object Globals:

  private val dirs = Obj(
    "destination" -> "/_site",
    "base" -> "/src/main/scala/site_template",
    "layout_dir" -> "/_layouts",
    "post_dir" -> "/_posts",
    "includes_dir" -> "/_includes",
    "sass_dir" -> "/_sass",
    "pluins_dir" -> "/_plugins"
  )

  private val reading = Obj(
    "include" -> Arr(".htaccess"),
    "exclude" -> Arr("build.sbt"),
    "keep_files" -> Arr(".git", ".svn"),
    "markdown_ext" -> "markdown,mkdown,mkdn,mkd,md",
    "encoding" -> "utf-8"
  )

  private val site = Obj(
    "title" -> "A Shiny New Website",
    "lang" -> "en",
    "root_url" -> "/",
    "description" -> "site description",
    "author" -> Obj(
      "name" -> "author name",
      "email" -> "author email"
    )
  )

  private val defaults = Obj(
    "paginate" -> false,
    "show_excerpts" -> true,
    "tag_layout" -> "tag",
    "date_format" -> "dd MMM, yyyy"
  )

  val globals = dirs.obj ++ reading.obj ++ site.obj ++ defaults.obj

  /** Support for data provided in _data folder. this will be in site("data") */
  private val config = yamlParser(dirs("base_dir").str + "/config.yml")
  for (key, value) <- config.obj do globals(key) = value

/** Should need to write the documentation for different options in the
  * config.yml
  *
  * posts_visibility: render all posts by default?
  *
  * log_level: the level of log
  *
  * default_url_template: the template of url used by posts without a speficied
  * template
  */

/** need to make a new list_map that will define the name of the list in yaml,
  * and the value name to assign to each of it's value.
  *
  * For example, if the yaml is like
  *
  * authors: [a, b, c] list_map: authors: author
  *
  * then the yaml will be rendered as if it were
  *
  * authors: [author: a, author: b, author: c]
  */
