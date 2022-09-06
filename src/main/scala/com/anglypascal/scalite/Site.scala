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

import com.anglypascal.scalite.utils.Cleaner
import com.anglypascal.scalite.plugins.SiteHooks

class Site(baseDir: String, dryRun: Boolean = false, cache: Boolean = false):
  /** */
  val logger = Logger("Site")

  private def getConfigs: MObj =
    val glbsObj =
      import Defaults.Directories.*
      import Defaults.Reading.*
      MObj(
        "base" -> baseDir,
        "collectionsDir" -> collectionsDir,
        "destination" -> destination,
        "layoutsDir" -> layoutsDir,
        "partialsDir" -> partialsDir,
        "sassDir" -> sassDir,
        "dataDir" -> dataDir,
        "pluginsDir" -> pluginsDir,
        // Which files to read
        "include" -> include,
        "exclude" -> exclude,
        "keepFiles" -> keepFiles, // give regex list
        "markdownExt" -> markdownExt,
        "textileExt" -> textileExt,
        "encoding" -> encoding,
        // Details about this website
        "title" -> Defaults.title,
        "description" -> Defaults.description,
        "lang" -> Defaults.lang,
        "rootUrl" -> "/",
        "author" -> Defaults.author,
        "paginate" -> Defaults.paginate,
        "showExcerpts" -> Defaults.showExceprts,
        "dateFormat" -> Defaults.dateFormat,
        "permalink" -> Defaults.permalink,
        "timeZone" -> Defaults.timeZone,
        // Details about building process
        "logLevel" -> 1
      )

    logger.trace("reading config file")

    yamlFileParser(baseDir + "/_config.yml") match
      case v: MObj => glbsObj update v
      case _ =>
        logger.error(
          s"yaml file ${baseDir}/_config.yml could not be read into an weejson Obj"
        )
    glbsObj

  private def initiatePlugins(pluginsDir: String, configs: MObj): Unit =
    PluginManager(pluginsDir, configs.extractOrElse("plugins")(MObj()))

  /** Process the assets */
  private def processAssets(
      assetD: String,
      dest: String,
      replace: Boolean,
      configs: MObj
  ): MObj =
    val dataMap = configs.extractOrElse("assets")(MObj())
    Assets(assetD, dest + "/assets")

  private def initiateConfigurables(
      configurables: List[(Configurable, MObj)],
      globals: IObj
  ): Unit =
    configurables foreach { (C, c) => C(c, globals) }

  private def collectData(dataDir: String, _configs: MObj): MObj =
    val obj = MObj()
    val configs = _configs.extractOrElse("data")(MObj())
    for f <- getListOfFilepaths(dataDir) do
      obj += getFileName(f) -> yamlFileParser(dataDir + f)
    obj

  private def getConfigurables(configs: MObj): List[(Configurable, MObj)] =
    val plugD =
      configs.getOrElse("base")(Defaults.Directories.base) +
        configs.getOrElse("pluginsDir")(Defaults.Directories.pluginsDir)
    initiatePlugins(plugD, configs)
    val configurables =
      List(ScopedDefaults, Converters, Layouts)
        ++ Clusters.clusters ++ List(Collections)
    configurables.map(C => (C, configs.extractOrElse(C.sectionName)(MObj())))

  def globals: IObj =

    val logger = Logger("initialization")
    val configs = getConfigs

    val configurables = getConfigurables(configs)

    import Defaults.{Directories => D}
    val _base = configs.getOrElse("base")(D.base)
    val _dest = _base + configs.getOrElse("destination")(D.destination)
    val _dataD = _base + configs.getOrElse("dataDir")(D.dataDir)
    val _assetD = _base + configs.getOrElse("assetsDir")(D.assetsDir)

    DirectoryReader(_dest)
    Pages.setup(_base)
    DateParser.setTimeZone(configs.extractOrElse("timeZone")(Defaults.timeZone))

    val replaceAssets =
      configs.extractOrElse("replaceAssets")(Defaults.replaceAssets)

    configs += "assets" -> processAssets(_assetD, _dest, replaceAssets, configs)
    configs += "data" -> collectData(_dataD, configs)

    val updates =
      val iobj = IObj(configs)
      SiteHooks.afterInits
        .map(_.apply(iobj))
        .foldLeft(MObj())(_ update _)

    val _globals = IObj(configs update updates)
    initiateConfigurables(configurables, _globals)

    SiteHooks.afterReads foreach { _.apply(_globals) }

    _globals

  def build(): Unit =
    val _globals = globals

    Cleaner(_globals)
    Collections.process(dryRun)
    Clusters.process(dryRun)
    Assets.copy(dryRun)
