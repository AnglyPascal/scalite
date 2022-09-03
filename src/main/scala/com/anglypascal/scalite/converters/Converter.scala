package com.anglypascal.scalite.converters

import scala.util.matching.Regex
import com.anglypascal.scalite.plugins.Plugin

/** Converter provides the support to convert files matching the extension regex
  * to html files. Can be extended to provide support for arbitrary language.
  */
trait Converter extends Plugin:

  /** filetype that this converter handles */
  val fileType: String

  /** The extensions of the files this converter is able to convert */
  val extensions: String

  def ext: Regex =
    ("(" + extensions
      .split(",").map(s => raw".*\." + s)
      .mkString("|") + ")").r

  /** Does this converter accepts the file? */
  def matches(filepath: String): Boolean =
    ext.matches(filepath)

  /** Specify the output filetype. TODO: What's the point? */
  def outputExt: String

  /** Convert a given string
    *
    * @param str
    *   the string to be converted. In our case, the main matter of the file
    * @returns
    *   - Left(e) where e is a ConverterException, or
    *   - Right(s) where s is the converted string.
    */
  def convert(str: String, filepath: String): String
  def convert(str: String): String = convert(str, "string input")

trait ConverterConstructor extends Plugin:
  val constructorName: String
  def apply(fileType: String, extensions: String, outputExt: String): Converter
