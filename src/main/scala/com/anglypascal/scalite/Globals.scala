package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DArr => IArr}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DArr => MArr}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.documents.DataFiles
import com.anglypascal.scalite.documents.Pages
import com.anglypascal.scalite.groups.Groups
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.utils.yamlFileParser

import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.utils.DateParser

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
    MObj(
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
    MObj(
      "include" -> include,
      "exclude" -> exclude,
      "keepFiles" -> keepFiles, // give regex list
      "markdownExt" -> markdownExt,
      "textileExt" -> textileExt,
      "encoding" -> encoding
    )

  /** Details about this website */
  private lazy val site =
    MObj(
      "title" -> Defaults.title,
      "description" -> Defaults.description,
      "lang" -> Defaults.lang,
      "rootUrl" -> "/",
      "author" -> Defaults.author,
      "paginate" -> Defaults.paginate,
      "showExcerpts" -> Defaults.showExceprts,
      "dateFormat" -> Defaults.dateFormat,
      "permalink" -> Defaults.permalink,
      "timeZone" -> Defaults.timeZone
    )

  /** FIXME what do with this? */
  private lazy val build =
    MObj(
      "logLevel" -> 1
    )

  private val configurables =
    List[Configurable](
      PluginManager,
      ScopedDefaults,
      Converters,
      Layouts,
      Groups,
      Collections
    )

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
    yamlFileParser(_base + "/_config.yml") match
      case v: MObj => v
      case _ =>
        logger.error(
          s"yaml file ${_base} could not be read into an weejson Obj"
        )
        MObj()

  private def getConfiguration(conf: Configurable): MObj =
    configs.extractOrElse(conf.sectionName)(MObj())

  /** Process the defaults fro the updated config */
  private def processDefaults() =
    logger.trace("setting the default configurations")
    val defMap = configs.extractOrElse("defaults")(MObj())

  /** Process the assets */
  private def processAssets =
    logger.trace("processing the assets")
    val dataMap = configs.extractOrElse("assets")(MObj())
    Assets(_base + _assetD, _dest + "/assets")

  /** Load all the plugins, defaults and custom */
  // private def loadPlugins(): Unit =
  //   logger.trace("loading the plugins and instantiating standalone objects")
  // default plugins

  /** Read the config, do all the initial stuff, return the global variables */
  def apply(base: String) =

    val glbsObj = MObj()
    dirs("base") = DStr(base)

    for key <- dirs.keys if configs.contains(key) do dirs(key) = configs(key)

    val interm = configurables.map(c => (c, getConfiguration(c)))

    Pages.setup(_base)
    DateParser.setTimeZone(configs.extractOrElse("timeZone")(Defaults.timeZone))

    processDefaults()

    glbsObj update dirs
    glbsObj update reading
    glbsObj update site

    glbsObj("assets") = processAssets
    // glbsObj("data") = DataFiles(_base + _dataD)

    for (key, value) <- configs do glbsObj(key) = value

    val _globals = IObj(glbsObj)

    interm.map(p => p._1(p._2, _globals))

    DirectoryReader(_base + _dest)

    _globals
