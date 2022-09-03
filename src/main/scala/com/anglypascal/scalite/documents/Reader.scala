package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DateParser.dateToString
import com.anglypascal.scalite.utils.DirectoryReader.readFile
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.frontMatterParser
import com.anglypascal.scalite.ScopedDefaults

import java.nio.file.Files
import java.nio.file.Paths

/** Document represents the pages of the site that are generated from the
  * templates and user created content files. This includes all mustache
  * templates, posts in markdown or other language supported by the Converters
  */
trait Reader:

  /** The base directory this file lies in */
  val parentDir: String

  /** The path to the file relative to the parent directory */
  val relativePath: String

  /** type of this file, used to determine the defaults */
  val rType: String

  /** path to the file */
  lazy val filepath: String = parentDir + relativePath

  /** Strip the filepath to get the filename */
  lazy val filename: String = getFileName(filepath)

  /** Read the front and main matter from the file */
  private lazy val (_frontMatter, _mainMatter) =
    val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r
    val scope = ScopedDefaults.getDefaults(filepath, rType)
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) =>
        scope update frontMatterParser(a)
        (scope, b)
      case _ => (scope, src)

  /** yaml front matter of the file */
  def frontMatter = _frontMatter

  /** main matter containing the contents of the file */
  def mainMatter = _mainMatter
