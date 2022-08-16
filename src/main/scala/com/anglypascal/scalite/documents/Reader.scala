package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.dateToString
import com.anglypascal.scalite.utils.readFile
import com.anglypascal.scalite.utils.yamlParser
import com.rallyhealth.weejson.v1.Obj

import java.nio.file.Files
import java.nio.file.Paths

/** Document represents the pages of the site that are generated from the
  * templates and user created content files. This includes all mustache
  * templates, posts in markdown or other language supported by the Converters
  *
  * @constructor
  *   creates a new document from the given filepath
  * @param filepath
  *   the path to the file
  */
trait Reader(val filepath: String):

  /** Strip the filepath to get the filename */
  private val filename: String = filepath.split("/").last.split(".").head

  /** read the file and get the string from it */
  private val src = readFile(filepath).toString

  /** Regex to match the YAML front matter and the remaining content */
  private val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r

  /** Match the regex and store the results in a weejson Value and content.
    */
  val (front_matter, main_matter) =
    src match
      case yaml_regex(a, b) => (yamlParser(a), b)
      case _                => (Obj(), src)

/** To provide support for extension methods on a Reader
  */
trait ReaderOps extends Reader:
  import org.joda.time.DateTime

  /** Gets the last modified time of a file in the given date format */
  def lastModifiedTime(dateFormat: String): String =
    val path = Paths.get(filepath)
    val modTime =
      Files.getLastModifiedTime(path).toInstant().getEpochSecond()
    val date = new DateTime(modTime)
    dateToString(date, dateFormat)

  val p = 2
