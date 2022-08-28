package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.DateParser.dateToString
import com.anglypascal.scalite.utils.DirectoryReader.readFile
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.yamlParser
import com.rallyhealth.weejson.v1.Obj

import java.nio.file.Files
import java.nio.file.Paths
import com.typesafe.scalalogging.Logger

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

  /** */
  private val logger = Logger("Reader")

  /** Strip the filepath to get the filename */
  private val filename: String = getFileName(filepath)

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
