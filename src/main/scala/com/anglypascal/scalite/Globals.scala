package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.collections.Posts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DataExtensions.extractOrElse
import com.anglypascal.scalite.data.DataExtensions.getOrElse
import com.anglypascal.scalite.layouts.*
import com.anglypascal.scalite.groups.*
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.yamlParser
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}
import com.anglypascal.scalite.assets.Assets
import com.anglypascal.scalite.converters.Identity
import com.anglypascal.scalite.collections.StaticPages
import com.anglypascal.scalite.collections.Drafts

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  *
  * TODO I really should define all these defaults in a separate immutable
  * object, so that I can call those for the getOrElse
  */
object Globals:

  /** Where stuff are */
  private lazy val dirs =
    import Defaults.Directories.*
    Obj(
      "base" -> base,
      "collectionsDir" -> collectionsDir,
      "destination" -> destination,
      "layoutsDir" -> layoutsDir,
      "includesDir" -> includesDir,
      "sassDir" -> sassDir,
      "dataDir" -> dataDir,
      "pluginsDir" -> pluginsDir
    )

  /** Which files to read */
  private lazy val reading =
    import Defaults.Reading.*
    Obj(
      "include" -> Defaults.Reading.include,
      "exclude" -> Defaults.Reading.exclude,
      "keepFiles" -> Defaults.Reading.keepFiles, // give regex list
      "markdownExt" -> Defaults.Reading.markdownExt,
      "textileExt" -> Defaults.Reading.textileExt,
      "encoding" -> Defaults.Reading.encoding
    )

  /** Details about this website */
  private lazy val site = Obj(
    "title" -> Defaults.title,
    "description" -> Defaults.description,
    "lang" -> Defaults.lang,
    "rootUrl" -> "/",
    "author" -> Defaults.author,
    "paginate" -> Defaults.paginate,
    "showExcerpts" -> Defaults.showExceprts,
    "dateFormat" -> Defaults.dateFormat,
    "permalinkTemplate" -> Defaults.permalinkTemplate
  )

  /** Defaults of the `collection` section. */
  private lazy val collections = Obj(
    "posts" -> Obj(
      "output" -> true,
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false,
      "permalinkTemplate" -> "/{{> item }}" // TODO how is item going to be rendered?
    ),
    "drafts" -> Obj(
      "output" -> false,
      "directory" -> dirs("collectionsDir").str,
      "sortBy" -> "dates",
      "toc" -> false,
      "permalinkTemplate" -> "/{{> item }}"
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

  /** Process the groups fro the updated config 
   *
   *  FIXME: pass in the dataMap
   *  */
  private def processAssets: Obj =
    val dataMap = configs.extractOrElse("assets")(MMap[String, Value]())
    Assets(
      dirs("base").str + dirs.getOrElse("assetsDir")(Defaults.Directories.assetsDir),
      dirs("destination").str + "/assets"
    )

  /** Load all the plugins, defaults and custom */
  private def loadPlugins(): Unit =
    // default plugins
    Converters.addConverter(Markdown)
    Converters.addConverter(Identity)
    Layouts.addEngine(MustacheLayout)

    // custom plugins
    val plugMap = configs.extractOrElse("plugins")(MMap[String, Value]())
    PluginManager(dirs("base").str + dirs("pluginsDir").str, DObj(plugMap))

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

    glbsObj("assets") = processAssets

    /** FIXME needs to be done before processing can happen LMAO
     */
    for (key, value) <- configs.obj do glbsObj(key) = value

    val _globals = DObj(glbsObj)

    // TODO collection templates
    Converters.modifyExtensions(extensions)
    Collections(
      dirs("base").str + dirs("collectionsDir").str,
      collections,
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
