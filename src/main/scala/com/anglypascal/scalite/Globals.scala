package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DArr => IArr}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DArr => MArr}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.documents.Pages
import com.anglypascal.scalite.groups.Clusters
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.plugins.PluginManager
import com.anglypascal.scalite.utils.DateParser
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.yamlFileParser
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.{Map => MMap}

def getConfigs(base: String): MObj =
  val logger = Logger("Globals")
  logger.trace("reading config file")

  yamlFileParser(base + "/_config.yml") match
    case v: MObj => v
    case _ =>
      logger.error(
        s"yaml file ${base}/_config.yml could not be read into an weejson Obj"
      )
      MObj()

def initiatePlugins(pluginsDir: String, configs: MObj): Unit =
  PluginManager(pluginsDir, configs.extractOrElse("plugins")(MObj()))

def getConfigurations(
    configurables: List[Configurable],
    configs: MObj
): List[(Configurable, MObj)] =
  configurables.map(C => (C, configs.extractOrElse(C.sectionName)(MObj())))

/** Process the assets */
def processAssets(assetD: String, dest: String, configs: MObj): MObj =
  val dataMap = configs.extractOrElse("assets")(MObj())
  Assets(assetD, dest + "/assets")

def initiateConfigurables(
    configurables: List[(Configurable, MObj)],
    globals: IObj
): Unit =
  configurables foreach { (C, c) => C(c, globals) }

def collectData(dataDir: String, _configs: MObj): MObj =
  val obj = MObj()
  val configs = _configs.extractOrElse("data")(MObj())
  for f <- getListOfFilepaths(dataDir) do
    obj += getFileName(f) -> yamlFileParser(dataDir + f)
  obj

def initialize(baseDir: String): IObj =

  val logger = Logger("initialization")

  // Where stuff are
  lazy val dirs =
    import Defaults.Directories.*
    MObj(
      "base" -> baseDir,
      "collectionsDir" -> collectionsDir,
      "destination" -> destination,
      "layoutsDir" -> layoutsDir,
      "partialsDir" -> partialsDir,
      "sassDir" -> sassDir,
      "dataDir" -> dataDir,
      "pluginsDir" -> pluginsDir
    )

  // Which files to read
  lazy val reading =
    import Defaults.Reading.*
    MObj(
      "include" -> include,
      "exclude" -> exclude,
      "keepFiles" -> keepFiles, // give regex list
      "markdownExt" -> markdownExt,
      "textileExt" -> textileExt,
      "encoding" -> encoding
    )

  // Details about this website
  lazy val site =
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

  // Details about building process
  lazy val build =
    MObj(
      "logLevel" -> 1
    )

  // Configurable objects
  lazy val configurables: List[Configurable] =
    List(ScopedDefaults, Converters, Layouts)
      ++ Clusters.clusters ++ List(Collections)

  val configs = getConfigs(baseDir)

  for
    key <- dirs.keys
    if configs.contains(key)
  do dirs(key) = configs(key)

  import Defaults.{Directories => D}
  val _base = dirs.getOrElse("base")(D.base)
  val _dest = _base + dirs.getOrElse("destination")(D.destination)
  val _dataD = _base + dirs.getOrElse("dataDir")(D.dataDir)
  val _assetD = _base + dirs.getOrElse("assetsDir")(D.assetsDir)
  val _plugD = _base + dirs.getOrElse("pluginsDir")(D.pluginsDir)

  DirectoryReader(_dest)
  initiatePlugins(_plugD, configs)
  Pages.setup(_base)
  DateParser.setTimeZone(configs.extractOrElse("timeZone")(Defaults.timeZone))

  val interm = getConfigurations(configurables, configs)

  val glbsObj = MObj()
  glbsObj update dirs
  glbsObj update reading
  glbsObj update site
  glbsObj += "assets" -> processAssets(_assetD, _dest, configs)
  glbsObj += "data" -> collectData(_dataD, configs)

  glbsObj update configs

  val globals = IObj(glbsObj)
  initiateConfigurables(interm, globals)

  globals
