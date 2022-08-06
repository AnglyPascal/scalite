package com.anglypascal.scalite.converters

import com.anglypascal.scalite.ConverterException

import scala.collection.mutable.Set
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

  /** Convert method to provide the logic of the conversion. */
  def convert(str: String): Either[ConverterException, String]

/** Companion object giving api to add new members to the set of converters
  * available to Post to render it's content
  */
object Converter:

  val converters = Set[Converter](Markdown)

  private def findConverter(ext: String): Option[Converter] =
    converters.filter(_.matches(ext)).headOption

  def hasConverter(ext: String): Boolean =
    findConverter(ext) != None

  def convert(
      str: String,
      filename: String
  ): Either[ConverterException, String] =
    findConverter(filename) match
      case Some(converter) => converter.convert(str)
      case None => Left(ConverterException("No converter defined for filetype"))

  def addConverter(conv: Converter): Unit = converters += conv
