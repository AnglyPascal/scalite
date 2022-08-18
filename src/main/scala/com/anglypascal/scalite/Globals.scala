package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.collections.Posts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
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
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  */
object Globals:

  /** Where stuff are */
  private lazy val dirs = Obj(
    "base" -> ".",
    "collectionsDir" -> "",
    "destination" -> "/_site",
    "layoutsDir" -> "/_layouts",
    "includesDir" -> "/_includes",
    "sassDir" -> "/_sass",
    "dataDir" -> "/_data",
    "pluginsDir" -> "/_plugins"
  )

  /** Which files to read */
  private lazy val reading = Obj(
    "include" -> Arr(".htaccess"),
    "exclude" -> Arr("build.sbt"),
    "keepFiles" -> Arr(".git", ".svn"), // give regex list
    "markdownExt" -> "markdown,mkdown,mkdn,mkd,md",
    "textileExt" -> "textile",
    "encoding" -> "utf-8"
  )

  /** Details about this website */
  private lazy val site = Obj(
    "title" -> "A Shiny New Website",
    "lang" -> "en",
    "rootUrl" -> "/",
    "description" -> "generic site description",
    "author" -> Obj(),
    "paginate" -> false,
    "showExcerpts" -> true,
    "dateFormat" -> "dd MMM, yyyy",
    "urlTemplate" -> "{{default}}"
  )

  /** Defaults of the `collection` section. */
  private lazy val collections = Obj(
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

  private lazy val build = Obj(
    "logLevel" -> 1
  )

  /** Load the configs from "/\_config.yml" file */
  private lazy val configs =
    yamlParser(dirs("base").str + "/_config.yml")

  /** Get the updated exteions for the converters. Will move this functionality
    * later to the Converters section
    */
  private def extensions =
    reading.obj
      .filter((s, _) => s.endsWith("Ext"))
      .map((s, v) =>
        val ext = v match
          case v: Str => v.str
          case v: Arr => v.arr.map(_.str).mkString(",")
          case _      => ""
        (s.dropRight(3), ext)
      )

  /** Process the collections from the updated config */
  private def processCollections() =
    configs.obj.remove("collections") match
      case Some(colObj): Some[Obj] =>
        colObj.obj.remove("collectionsDir") match
          case Some(s): Some[Str] => dirs("collectionsDir") = s.str
          case _                  => ()
        for (key, value) <- colObj.obj do collections(key) = value
      case _ => ()

  /** Process the defaults fro the updated config */
  private def processDefaults() =
    configs.obj.remove("defaults") match
      case Some(colObj): Some[Obj] => ()
      case _                       => ()

  /** Process the groups fro the updated config */
  private def processGroups() =
    configs.obj.remove("groups") match
      case Some(colObj): Some[Obj] => ()
      case _                       => ()

  /** Process the groups fro the updated config */
  private def processAssets() =
    configs.obj.remove("data") match
      case Some(colObj): Some[Obj] => ()
      case _                       => ()

  /** Load all the plugins, defaults and custom */
  private def loadPlugins(): Unit =
    // default plugins
    Converters.addConverter(Markdown)
    Collections.addToCollection(Posts)
    Layouts.addEngine(MustacheLayout)
    // custom plugins
    configs.obj.remove("plugins") match
      case Some(obj): Some[Obj] =>
        PluginManager(dirs("pluginsDir").str, DObj(obj))
      case _ => ()

  /** Read the config, do all the initial stuff, return the global variables */
  def apply(base: String) =

    val glbsObj = Obj()
    dirs("base") = base
    dirs("collecionsDir") = base

    glbsObj.obj ++= dirs.obj
    glbsObj.obj ++= reading.obj
    glbsObj.obj ++= site.obj

    loadPlugins()

    processDefaults()
    processCollections()
    processGroups()
    processAssets()

    for (key, value) <- configs.obj do glbsObj(key) = value

    val _globals = DObj(glbsObj)

    // TODO collection templates
    Converters.modifyExtensions(extensions)
    Collections(
      dirs("base").str + dirs("collectionsDir").str,
      DObj(collections),
      _globals
    )

    _globals

/** need to make a new list_map that will define the name of the list in yaml,
  * and the value name to assign to each of it's value.
  *
  * For example, if the yaml is like `authors: [a, b, c]` list_map: authors:
  * author then the yaml will be rendered as if it were authors: [author: a,
  * author: b, author: c]
  */
