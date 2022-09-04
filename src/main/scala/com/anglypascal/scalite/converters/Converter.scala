package com.anglypascal.scalite.converters

import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.Colors.*

import scala.util.matching.Regex

/** Converter provides the support to convert files matching the extension regex
  * to html files. Can be extended to provide support for arbitrary language.
  */
trait Converter:

  /** filetype that this converter handles */
  val fileType: String

  /** A comma separated list of extensions reprseneting the files this converter
    * is able to convert.
    */
  val extensions: String

  private def ext: Regex =
    ("(" + extensions.trim
      .split(",")
      .map(_.trim.toLowerCase)
      .map(s => raw".*\." + s)
      .mkString("|") + ")").r

  /** Does this converter accepts the file? */
  def matches(filepath: String): Boolean =
    ext.matches(filepath.toLowerCase)

  /** Specify the output filetype. */
  def outputExt: String

  /** Convert a given string
    *
    * @param str
    *   the string to be converted. In our case, the main matter of the file
    * @param filepath
    *   the source filepath, used to search for an appropriate converter
    * @returns
    *   converted string
    */
  def convert(str: String, filepath: String): String

  /** Converts the given string using the default markdown converter
    */
  def convert(str: String): String = convert(str, "stringInput.md")

  override def toString(): String =
    BLUE(fileType) + ": " + extensions.split(",").map(_.trim).mkString(", ")

/** Constructor for a Converter */
trait ConverterConstructor extends Plugin:

  /** Name of the converter */
  val constructorName: String

  /** Create a new Constructor to be used in runtime */
  def apply(fileType: String, extensions: String, outputExt: String): Converter
