package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.frontMatterParser
import com.anglypascal.scalite.ScopedDefaults

import com.anglypascal.scalite.data.mutable.DObj

trait SourceFile:
  
  val relativePath: String

  val parentDir: String

  inline def filepath: String = parentDir + relativePath

  inline def filename: String = getFileName(filepath)

  protected val frontMatter: DObj

  protected lazy val mainMatter: String

  protected lazy val shouldConvert: Boolean

