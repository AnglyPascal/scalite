package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DateParser.dateToString
import com.anglypascal.scalite.utils.DirectoryReader.readFile
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.frontMatterParser
import com.anglypascal.scalite.ScopedDefaults

import java.nio.file.Files
import java.nio.file.Paths
import com.anglypascal.scalite.data.mutable.DObj

object Reader:

  private val yaml_regex = raw"\A---\n?([\s\S\n]*?)---\n?([\s\S\n]*)".r

  def frontMatter(rType: String, filepath: String): DObj = 
    val scope = ScopedDefaults.getDefaults(filepath, rType)
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) =>
        scope += "shouldConvert" -> true
        scope update frontMatterParser(a)
      case _ => scope += "shouldConvert" -> false

  def mainMatter(filepath: String): String =
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) => b.trim
      case _                => src.trim


trait SourceFile:
  
  val relativePath: String

  val parentDir: String

  def filepath: String = parentDir + relativePath

  def filename: String = getFileName(filepath)

  protected val shouldConvert: Boolean
