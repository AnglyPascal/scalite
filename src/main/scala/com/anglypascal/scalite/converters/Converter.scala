package com.anglypascal.scalite.converters

import com.anglypascal.scalite.ConverterException

import scala.collection.mutable.Set
import scala.util.matching.Regex
import com.typesafe.scalalogging.Logger

/** Converter provides the support to convert files matching the extension regex
  * to html files. Can be extended to provide support for arbitrary language.
  */
trait Converter:

  /** The extensions of the files this converter is able to convert */
  def ext: Regex

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
  Converter.addConverter(this)

/** Companion object giving api to add new members to the set of converters
  * available to Post to render it's content
  */
object Converter:

  /** Set of all the avaiable converters. When an object implements the
    * Converter trait, it gets added to this set.
    */
  val converters = Set[Converter]()

  private val logger = Logger("Converter")

  /** Private method that finds the correct converter for a given file
    *
    * @param ext
    *   the path to the file to be converted
    * @return
    *   Some(c) if c accepts filetypes matching ext None if no such converter is
    *   available
    */
  private def findConverter(ext: String): Option[Converter] =
    converters.filter(_.matches(ext)).headOption

  /** Checks if there is a converter avaiable for thei given filepath
    * @param ext
    *   the path to the file to be converted
    * @return
    *   true if there's a converter accepting this ext; false otherwise
    */
  def hasConverter(ext: String): Boolean =
    findConverter(ext) match
      case None =>
        logger.warn(s"Converter could not be found for file $ext.")
        false
      case Some(conv) =>
        logger.debug(
          s"Converter object {} found for file $ext.",
          conv.getClass.getName
        )
        true

  /** Convert a given filepath with an appropriate converter if it exists
    *
    * @param str
    *   the string to be converted. In our case, the main matter of the file
    * @param filepath
    *   the path to the file containing the extension. Used to find appropriate
    *   converter
    * @returns
    *   - Left(e) where e is a ConverterException, or
    *   - Right(s) where s is the converted string.
    */
  def convert(
      str: String,
      filepath: String
  ): String =
    findConverter(filepath) match
      case Some(converter) =>
        converter.convert(str, filepath)
      case None =>
        logger.warn(
          "Converter could not be found, " +
            s"so no conversion was made for file $filepath."
        )
        str

  /** Method to add a new converter to the converters set. */
  def addConverter(conv: Converter): Unit = converters += conv
