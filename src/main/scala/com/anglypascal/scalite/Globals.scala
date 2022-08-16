package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.groups.*
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.yamlParser
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value

import scala.collection.mutable.LinkedHashMap

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  */
object Globals:

  private val glbsObj = Obj()

  private val dirs = Obj(
    "base" -> "/src/main/scala/site_template",
    "destination" -> "/_site",
    "layoutDir" -> "/_layouts",
    "collectionsDir" -> "/src/main/scala/site_template",
    "includesDir" -> "/_includes",
    "sassDir" -> "/_sass",
    "dataDir" -> "/_data",
    "pluginsDir" -> "/_plugins"
  )

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
    "description" -> "generic site description",
    "author" -> Obj(),
    "paginate" -> false,
    "show_excerpts" -> true,
    "date_format" -> "dd MMM, yyyy"
  )

  glbsObj.obj ++= dirs.obj
  glbsObj.obj ++= reading.obj
  glbsObj.obj ++= site.obj

  /** Support for data provided in _data folder. This will be in site("data") */
  private val configs = yamlParser(dirs("base_dir").str + "/config.yml")

  private val extensions =
    reading.obj
      .filter((s, _) => s.endsWith("Ext"))
      .map((s, v) =>
        val ext = v match
          case v: Str => v.str
          case v: Arr => v.arr.map(_.str).mkString(",")
          case _      => ""
        (s.dropRight(3), ext)
      )

  Converters.modifyExtensions(extensions)
  // TODO collection templates

  configs.obj.remove("plugins") match
    case Some(obj): Some[Obj] =>
      PluginManager(dirs("pluginsDir").str, DObj(obj))
    case _ => ()

  private val collections = Obj(
    "posts" -> Obj(
      "output" -> true,
      "folder" -> "/_posts",
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false
    ),
    "drafts" -> Obj(
      "output" -> false,
      "folder" -> "/_drafts",
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false
    )
  )

  configs.obj.remove("collections") match
    case Some(colObj): Some[Obj] =>
      colObj.obj.remove("collectionsDir") match
        case Some(s): Some[Str] => dirs("collectionsDir") = s.str
        case _                  => ()

      for (key, value) <- colObj.obj do collections(key) = value
    case _ => ()

  Collections(dirs("collectionsDir").str, DObj(collections), globals)

  configs.obj.remove("default") match
    case Some(colObj): Some[Obj] => ()
    case _                       => ()

  configs.obj.remove("groups") match
    case Some(colObj): Some[Obj] => ()
    case _                       => ()

  for (key, value) <- configs.obj do glbsObj(key) = value

  private val _globals = DObj(glbsObj)
  def globals = _globals

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
