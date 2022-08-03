package com.anglypascal.scalite.converters

import com.anglypascal.scalite.ConverterException

import scala.util.matching.Regex

/** Converter trait that can be extended to provide support for other markup
  * languages or custom languages.
  */
trait Converter:

  /** The extensions of the files this converter is able to convert */
  def ext: Regex

  /** Does this converter accepts the file? */
  def matches(filename: String): Boolean =
    ext.matches(filename)

  /** Specify the output filetype. */
  def outputExt: String

  /** Convert method to provide the logic of the conversion.
   */ def convert(str: String): Either[ConverterException, String]
