package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.extractOrElse
import com.anglypascal.scalite.data.DataExtensions.getOrElse
import com.anglypascal.scalite.data.immutable.{DArr => IArr}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.immutable.{DataAST => IAST}
import com.anglypascal.scalite.data.mutable.{DArr => MArr}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.documents.DataFiles
import com.anglypascal.scalite.documents.Pages
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.utils.yamlFileParser
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}
import com.anglypascal.scalite.groups.Groups

/** Defines the global variables and default configurations. Everything can be
  * overwritten in "/\_config.yml" file
  *
  * FIXME: Turn it into a function or a class. This object would otherwise live
  * forever during the program life time, where it's no longer needed
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

  /** FIXME what do with this? */
  private lazy val build = Obj(
    "logLevel" -> 1
  )

  private val configurables =
    List[Configurable](PluginManager, Converters, Groups, Collections)

  import Defaults.Directories
  private def _base = dirs.getOrElse("base")(Directories.base)
  private def _dest = dirs.getOrElse("destination")(Directories.destination)
  private def _layD = dirs.getOrElse("layoutsDir")(Directories.layoutsDir)
  private def _parD = dirs.getOrElse("partialsDir")(Directories.partialsDir)
  private def _plugD = dirs.getOrElse("pluginsDir")(Directories.pluginsDir)
  private def _dataD = dirs.getOrElse("dataDir")(Directories.dataDir)
  private def _assetD = dirs.getOrElse("assetsDir")(Directories.assetsDir)
  private def _colD =
    dirs.getOrElse("collectionsDir")(Directories.collectionsDir)

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

  private def processScopedDefaults() =
    logger.trace("setting up scoped defaults")
    configs.obj.remove("defaults").getOrElse(null) match
      case v: Arr => ScopedDefaults(_base, v)
      case _      => ()

  private def getConfiguration(conf: Configurable): MObj =
    MObj(configs.extractOrElse(conf.sectionName)(Obj()))

  /** Process the defaults fro the updated config */
  private def processDefaults() =
    logger.trace("setting the default configurations")
    val defMap = configs.extractOrElse("defaults")(MMap[String, Value]())

  /** Process the assets */
  private def processAssets: Obj =
    logger.trace("processing the assets")
    val dataMap = configs.extractOrElse("assets")(MMap[String, Value]())
    Assets(_base + _assetD, _dest + "/assets")

  /** Load all the plugins, defaults and custom */
  private def loadPlugins(): Unit =
    logger.trace("loading the plugins and instantiating standalone objects")
    // default plugins

  /** Read the config, do all the initial stuff, return the global variables */
  def apply(base: String) =

    val glbsObj = MMap[String, Value]()
    dirs("base") = base

    for key <- dirs.obj.keys if configs.obj.contains(key) do
      dirs(key) = configs(key)

    val interm = configurables.map(c => (c, getConfiguration(c)))

    loadPlugins()
    Pages.setup(_base)
    URL.setup(configs.extractOrElse("timeZone")(Defaults.timeZone))

    processDefaults()

    glbsObj ++= dirs.obj
    glbsObj ++= reading.obj
    glbsObj ++= site.obj

    glbsObj("assets") = processAssets
    glbsObj("data") = DataFiles(_base + _dataD)

    for (key, value) <- configs.obj do glbsObj(key) = value

    val _globals = IObj(glbsObj)

    interm.map(p => p._1(p._2, _globals))

    DirectoryReader(_base + _dest)
    Layouts(_base + _layD, _base + _parD)

    _globals
