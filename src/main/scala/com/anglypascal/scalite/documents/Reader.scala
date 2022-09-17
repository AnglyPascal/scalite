package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DateParser.dateToString
import com.anglypascal.scalite.utils.DirectoryReader.readFile
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.frontMatterParser
import com.anglypascal.scalite.ScopedDefaults

import java.nio.file.Files
import java.nio.file.Paths
import com.anglypascal.scalite.data.mutable.DObj

/** Document represents the pages of the site that are generated from the
  * templates and user created content files. This includes all mustache
  * templates, posts in markdown or other language supported by the Converters
  */
trait Reader:

  /** type of this file, used to determine the defaults */
  protected val rType: String

  /** path to the file */
  protected val filepath: String

  /** yaml front matter of the file */
  def frontMatter: DObj

  /** main matter containing the contents of the file */
  def mainMatter: String

class StrictReader(protected val rType: String, protected val filepath: String)
    extends Reader:

  /** Read the front and main matter from the file */
  private lazy val (_frontMatter, _mainMatter) =
    val yaml_regex = raw"\A---\n?([\s\S\n]*?)---\n?([\s\S\n]*)".r
    val scope = ScopedDefaults.getDefaults(filepath, rType)
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) =>
        scope += "shouldConvert" -> true
        scope update frontMatterParser(a)
        (scope, b.trim)
      case _ => (scope += "shouldConvert" -> false, src.trim)

  def frontMatter: DObj = _frontMatter
  def mainMatter: String = _mainMatter

class LazyReader(protected val rType: String, protected val filepath: String)
    extends Reader:

  private val yaml_regex = raw"\A---\n?([\s\S\n]*?)---\n?([\s\S\n]*)".r

  /** Read the front and main matter from the file */
  private lazy val _frontMatter =
    val scope = ScopedDefaults.getDefaults(filepath, rType)
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) =>
        scope += "shouldConvert" -> true
        scope update frontMatterParser(a)
      case _ => scope += "shouldConvert" -> false

  /** Read the front and main matter from the file */
  private lazy val _mainMatter =
    val src = readFile(filepath)
    src match
      case yaml_regex(a, b) => b.trim
      case _                => src.trim

  def frontMatter: DObj = _frontMatter
  def mainMatter: String = _mainMatter


trait SourceFile:
  
  val relativePath: String

  val parentDir: String

  def filepath: String = parentDir + relativePath

  def filename: String = getFileName(filepath)

  protected val shouldConvert: Boolean
