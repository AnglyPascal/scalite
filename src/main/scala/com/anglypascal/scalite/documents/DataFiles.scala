package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.yamlFileParser
import com.rallyhealth.weejson.v1.Obj

object DataFiles:

  def apply(dir: String) =
    val obj = Obj()
    for f <- getListOfFilepaths(dir) do
      obj(getFileName(f)) = yamlFileParser(dir + f)
    obj
