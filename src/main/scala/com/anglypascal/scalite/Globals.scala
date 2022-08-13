package com.anglypascal.scalite

import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.utils.yamlParser
import com.anglypascal.scalite.groups.*

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str}
import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.utils.DObj
import com.anglypascal.scalite.converters.Converters

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  */
object Globals:

  private val glbsObj = Obj()

  private val dirs = Obj(
    "destination" -> "/_site",
    "base" -> "/src/main/scala/site_template",
    "layoutDir" -> "/_layouts",
    "postDir" -> "/_posts",
    "includesDir" -> "/_includes",
    "sassDir" -> "/_sass",
    "pluginsDir" -> "/_plugins"
  )

  /** This should be in a different section
    */
  private val reading = Obj(
    "include" -> Arr(".htaccess"),
    "exclude" -> Arr("build.sbt"),
    "keepFiles" -> Arr(".git", ".svn"),
    "markdownExt" -> "markdown,mkdown,mkdn,mkd,md",
    "textileExt" -> "textile",
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

  glbsObj.obj ++= dirs.obj
  glbsObj.obj ++= reading.obj
  glbsObj.obj ++= site.obj
  glbsObj.obj ++= defaults.obj

  /** Support for data provided in _data folder. This will be in site("data") */
  private val config = yamlParser(dirs("base_dir").str + "/config.yml")
  for (key, value) <- config.obj do glbsObj(key) = value

  val globals = DObj(glbsObj)

  val extensions =
    reading.obj
      .filter((s, _) => s.endsWith("Ext"))
      .map((s, v) => 
          val ext = v match
            case v: Str => v.str
            case v: Arr => v.arr.map(_.str).mkString(",")
            case _ => ""
          (s.dropRight(3), ext))

  Converters.modifyExtensions(extensions)

  /** The values for collection will be separated here and sent to the
    * constructor of Collection object
    */

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
