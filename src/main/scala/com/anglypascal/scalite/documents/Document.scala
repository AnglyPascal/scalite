package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.*

import scala.io.Source
import scala.util.matching.Regex
import _root_.com.rallyhealth.weejson.v1._

/** Document represents the pages of the site that are generated from the
  * templates and user created content files. This includes all mustache
  * templates, posts in markdown or other language supported by the Converters
  *
  * @constructor
  *   creates a new document from the given filename
  * @param filename
  *   the name of the file
  */
trait Document(filename: String, globals: Obj) extends Page:

  /** read the file and store into a Source */
  private val src = readFile(filename).toString

  /** Regex to match the YAML front matter and the remaining content */
  private val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r

  /** Match the regex and store the results in a weejson Value and content.
    *
    * TODO: This is inefficient. write a custom parser that cuts the front
    * matter and returns the leftover source
    */
  val (front_matter, main_matter) =
    src match
      case yaml_regex(a, b) => (yamlParser(a), b)
      case _                => (Obj(), src)

  /** Get the parent layout name, if it exists. Layouts might not have a parent
    * layout, but each post needs to have one.
    *
    * TODO: In the Posts class, check if layout is empty, and throw exception
    */
  val parent_name =
    if front_matter.obj.contains("layout") then
      front_matter("layout") match
        case s: Str => s.str
        case _      => ""
    else ""
