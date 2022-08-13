package com.anglypascal.scalite.converters

import com.anglypascal.scalite.ConverterException

import scala.util.matching.Regex
import com.typesafe.scalalogging.Logger

/** Converter provides the support to convert files matching the extension regex
  * to html files. Can be extended to provide support for arbitrary language.
  */
trait Converter:

  /** filetype that this converter handles */
  val fileType: String

  /** The extensions of the files this converter is able to convert */
  private var _ext: Regex = _
  def ext: Regex = ext

  /** Set the extensions */
  def setExt(exts: String): Unit =
    val s = exts.split(",").mkString("|")
    _ext = (raw"*\.(" + s + ")").r

  /** Does this converter accepts the file? */
  def matches(filepath: String): Boolean =
    ext.matches(filepath)

  /** Specify the output filetype. */
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

  /** Add the newly defined converter object to Converter.converters */
  Converters.addConverter(this)
