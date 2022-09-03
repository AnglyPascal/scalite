package com.anglypascal.scalite.documents

import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.yamlFileParser
import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults

object DataFiles extends Configurable:

  val sectionName: String = "data"

  val obj = MObj()

  def apply(configs: MObj, globals: IObj): Unit =
    val dir =
      globals.getOrElse("base")(Defaults.Directories.base) +
        globals.getOrElse("dataDir")(Defaults.Directories.dataDir)

    for f <- getListOfFilepaths(dir) do
      obj(getFileName(f)) = yamlFileParser(dir + f)
