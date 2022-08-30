package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Identity
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DataAST
import com.anglypascal.scalite.data.DataExtensions.extractOrElse
import com.anglypascal.scalite.data.DataExtensions.getOrElse
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.documents.DataFiles
import com.anglypascal.scalite.groups.{Tags, Categories, Groups}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.utils.yamlFileParser
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  *
  * FIXME: Turn it into a function or a class. This object would otherwise live forever during
  * the program life time, where it's no longer needed
  */
object Globals:

  private val logger = Logger("Globals")

  /** Where stuff are */
  private lazy val dirs =
    import Defaults.Directories.*
    Obj(
      "base" -> base,
      "collectionsDir" -> collectionsDir,
      "destination" -> destination,
      "layoutsDir" -> layoutsDir,
      "partialsDir" -> partialsDir,
      "sassDir" -> sassDir,
      "dataDir" -> dataDir,
      "pluginsDir" -> pluginsDir
    )

  /** Which files to read */
  private lazy val reading =
    import Defaults.Reading.*
    Obj(
      "include" -> include,
      "exclude" -> exclude,
      "keepFiles" -> keepFiles, // give regex list
      "markdownExt" -> markdownExt,
      "textileExt" -> textileExt,
      "encoding" -> encoding
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
    "permalink" -> Defaults.permalink
  )

  /** Defaults of the `collection` section. */
  private lazy val collections =
    import Defaults.Posts
    import Defaults.Drafts
    import Defaults.Statics
    Obj(
      "posts" -> Obj(
        "output" -> Posts.output,
        "folder" -> Posts.folder,
        "name" -> Posts.name,
        "directory" -> Posts.directory,
        "sortBy" -> Posts.sortBy,
        "toc" -> Posts.toc,
        "permalink" -> Posts.permalink,
        "layout" -> Posts.layout
      ),
      "drafts" -> Obj(
        "output" -> Drafts.output,
        "folder" -> Drafts.folder,
        "name" -> Drafts.name,
        "directory" -> Drafts.directory,
        "sortBy" -> Drafts.sortBy,
        "toc" -> Drafts.toc,
        "permalink" -> Drafts.permalink,
        "layout" -> Drafts.layout
      ),
      "statics" -> Obj(
        "output" -> Statics.output,
        "folder" -> Statics.folder,
        "name" -> Statics.name,
        "directory" -> Statics.directory,
        "sortBy" -> Statics.sortBy,
        "toc" -> Statics.toc,
        "permalink" -> Statics.permalink,
        "layout" -> Statics.layout
      )
    )

  /** FIXME what do with this? */
  private lazy val build = Obj(
    "logLevel" -> 1
  )

  private def _base =
    dirs.getOrElse("base")(Defaults.Directories.base)
  private def _dest =
    dirs.getOrElse("destination")(Defaults.Directories.destination)
  private def _colD =
    dirs.getOrElse("collectionsDir")(Defaults.Directories.collectionsDir)
  private def _layD =
    dirs.getOrElse("layoutsDir")(Defaults.Directories.layoutsDir)
  private def _parD =
    dirs.getOrElse("partialsDir")(Defaults.Directories.partialsDir)
  private def _plugD =
    dirs.getOrElse("pluginsDir")(Defaults.Directories.pluginsDir)
  private def _dataD =
    dirs.getOrElse("dataDir")(Defaults.Directories.dataDir)
  private def _assetD =
    dirs.getOrElse("assetsDir")(Defaults.Directories.assetsDir)

  /** Load the configs from "/\_config.yml" file */
  private lazy val configs =
    logger.trace("reading config file")
    yamlFileParser(dirs("base").str + "/_config.yml") match
      case v: Obj => v
      case _ =>
        logger.error(
          s"yaml file ${dirs("base").str} could not be read into an weejson Obj"
        )
        Obj()

  /** Get the updated exteions for the converters. Will move this functionality
    * later to the Converters section
    */
  private def extensions =
    logger.trace("reading ml extension modifiers")
    reading.obj
      .filter((s, _) => s.endsWith("Ext"))
      .map((s, v) =>
        val ext = v match
          case v: Str => v.str
          case v: Arr => v.arr.map(_.str).mkString(",")
          case _      => ""
        (s.dropRight(3), ext)
      )

  private def processScopedDefaults() =
    logger.trace("setting up scoped defaults")
    configs.obj.remove("defaults").getOrElse(null) match
      case v: Arr => ScopedDefaults(_base, v)
      case _      => ()

  /** Process the collections from the updated config */
  private def processCollections() =
    logger.trace("setting up configs for collections")
    val colMap = configs.extractOrElse("collections")(MMap[String, Value]())
    colMap.remove("collectionsDir") match
      case Some(s) => dirs("collectionsDir") = s
      case None    => ()
    for (key, value) <- colMap do collections(key) = value

  /** Process the defaults fro the updated config */
  private def processDefaults() =
    logger.trace("setting the default configurations")
    val defMap = configs.extractOrElse("defaults")(MMap[String, Value]())

  /** Process the groups fro the updated config */
  private def processGroups() =
    logger.trace("setting up configs for groups")
    val grpMap = configs.extractOrElse("groups")(MMap[String, Value]())

  /** Process the groups fro the updated config
    *
    * FIXME: pass in the dataMap
    */
  private def processAssets: Obj =
    logger.trace("processing the assets")
    val dataMap = configs.extractOrElse("assets")(MMap[String, Value]())
    Assets(_base + _assetD, _dest + "/assets")

  /** Load all the plugins, defaults and custom */
  private def loadPlugins(): Unit =
    logger.trace("loading the plugins and instantiating standalone objects")
    // default plugins
    val dataAST = DataAST
    Converters.addConverter(Markdown)
    Layouts.addEngine(MustacheLayouts)
    Groups.addNewGroup(Tags)
    Groups.addNewGroup(Categories)

    // custom plugins
    val plugMap = configs.extractOrElse("plugins")(MMap[String, Value]())
    PluginManager(_base + _plugD, DObj(plugMap))

  /** Read the config, do all the initial stuff, return the global variables */
  def apply(base: String) =

    val glbsObj = MMap[String, Value]()
    dirs("base") = base

    for key <- dirs.obj.keys if configs.obj.contains(key) do
      dirs(key) = configs(key)

    loadPlugins()

    processDefaults()
    processCollections()
    processGroups()

    glbsObj ++= dirs.obj
    glbsObj ++= reading.obj
    glbsObj ++= site.obj

    glbsObj("assets") = processAssets
    glbsObj("data") = DataFiles(_base + _dataD)

    for (key, value) <- configs.obj do glbsObj(key) = value

    val _globals = DObj(glbsObj)

    DirectoryReader(_base + _dest)
    Converters.modifyExtensions(extensions)
    Collections(_base + _colD, collections, _globals)
    Layouts(_base + _layD, _base + _parD)

    _globals
