package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.collections.Posts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DataExtensions.extractOrElse
import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.groups.*
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.yamlParser
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}

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
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false,
      "permalink" -> "/{{item}}"
    ),
    "drafts" -> Obj(
      "output" -> false,
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false,
      "permalink" -> "/{{item}}"
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
    val colMap = configs.extractOrElse("collections")(MMap[String, Value]())
    dirs("collectionsDir") = colMap.extractOrElse("collectionsDir")("")
    for (key, value) <- colMap.obj do collections(key) = value

  /** Process the defaults fro the updated config */
  private def processDefaults() =
    val defMap = configs.extractOrElse("defaults")(MMap[String, Value]())

  /** Process the groups fro the updated config */
  private def processGroups() =
    val grpMap = configs.extractOrElse("groups")(MMap[String, Value]())

  /** Process the groups fro the updated config */
  private def processAssets() =
    val dataMap = configs.extractOrElse("data")(MMap[String, Value]())

  /** Load all the plugins, defaults and custom */
  private def loadPlugins(): Unit =
    // default plugins
    Converters.addConverter(Markdown)
    Collections.addToCollection(Posts)
    Layouts.addEngine(MustacheLayout)
    // custom plugins
    val plugMap = configs.extractOrElse("plugins")(MMap[String, Value]())
    PluginManager(dirs("pluginsDir").str, DObj(plugMap))

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
